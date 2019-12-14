package com.airtec.ui.tripcustomers

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airtec.R
import com.airtec.activities.BaseActivity
import com.airtec.customviews.ProgressHUD
import com.airtec.model.CustomerDetail
import com.airtec.model.KeyValue
import com.airtec.model.postdelivery.*
import com.airtec.network.NetworkInterface
import com.airtec.ui.adapter.FTADataBinder
import com.airtec.ui.adapter.GenericExpandableListAdaptor
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_tripcustomer.empty_list_item
import kotlinx.android.synthetic.main.fragment_tripcustomer.expandableListView
import kotlinx.android.synthetic.main.fragment_tripcustomer.search
import java.util.ArrayList

class TripCustomersFragment : Fragment() {
    private var tripNumber: String? = null

    private var profileName:String? =""

    private lateinit var adapter: GenericExpandableListAdaptor<String, KeyValue>
    val wikiApiServe by lazy {
        NetworkInterface.create()
    }
    var disposable: Disposable? = null

    var medicalBenefitsGroup =
        ArrayList<String>()
    var childList: ArrayList<ArrayList<KeyValue>> =
        ArrayList<ArrayList<KeyValue>>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_tripcustomer, container, false)

        return root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profileName = activity!!.intent.extras!!.getString("USER")

        tripNumber = arguments?.getString("tripNumber")

       search.text = "Trip Customers:"+tripNumber
        val mDrawableTutorial =
            intArrayOf(R.id.group_header_arrow, R.drawable.minus_icon, R.drawable.plus_icon)
        adapter = GenericExpandableListAdaptor<String, KeyValue>(
            medicalBenefitsGroup,
            childList,
            activity,
            R.layout.header_benefits_item,
            R.layout.general_expandable_list_item,
            mDrawableTutorial,
            groupDatabinder,
            childDatabinder
        )

        expandableListView.setAdapter(adapter)

        getCustomerDetails(tripNumber!!)
    }

    private fun getCustomerDetails(tripNumber: String) {
        var activity = activity as BaseActivity

        if (activity.isNetworkAvailable(activity)) {
            val msg: String = getResources().getString(R.string.msg_please_wait)
            ProgressHUD.show(activity, msg, true, false, null)

            disposable =
                wikiApiServe.getCustomerDetails(tripNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> showResult(result) },
                        { error -> showError(error.message) }
                    )
        } else

            showError("Intenet connection unavailable")
    }

    private fun showError(message: String?) {

        var activity = activity as BaseActivity

        ProgressHUD.dismisss()
        // activity.basicAlert(message);

        showEmptyView(getString(R.string.no_items))



    }

    private fun showResult(token: Any) {
        ProgressHUD.dismisss()

        val gson = GsonBuilder().create()
        val Model = gson.fromJson(token as String, Array<CustomerDetail>::class.java).toList()
        if (Model.size == 0)
            showEmptyView(getString(R.string.no_items))
        else{
            populateGroupAndChild(Model)

            if(medicalBenefitsGroup.size ==0){

                val footer = View.inflate(activity, R.layout.update_status, null)

                // Add the footer before the setAdapter() method
                // Add the footer before the setAdapter() method
                expandableListView.addFooterView(footer)
                footer.findViewById<TextView>(R.id.update).setText("Return back to office ")

                footer.findViewById<TextView>(R.id.update).setOnClickListener({
                    basicAlert("Item loaded.Do you want to update the status?")

                })
            }

        }
    }

    private fun showEmptyView(string: String) {
        view!!.findViewById<View>(R.id.empty_list_item).setVisibility(View.VISIBLE)
        empty_list_item.text = string
        medicalBenefitsGroup.clear()
        childList.clear()
        adapter.notifyDataSetChanged()



    }

    private fun populateGroupAndChild(branchList: List<CustomerDetail>) {
        medicalBenefitsGroup.clear()
        childList.clear()
        for (item in branchList) {
            if(TextUtils.isEmpty(item.tripNumber )){

               break
            }
            val innerCHildList: ArrayList<KeyValue> =
                ArrayList<KeyValue>()
            medicalBenefitsGroup.add(item.tripNumber)
                innerCHildList.add(
                    generateEachCellItem(
                        getString(R.string.tripNumber),
                        item.tripNumber
                    )
                )

            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.cust_acc_num),
                    item.custAccountNumber
                )
            )
            innerCHildList.add(generateEachCellItem(getString(R.string.cust_acc_id), item.custAccountID))
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.cust_name),
                    item.custName
                )
            )
            innerCHildList.add(generateEachCellItem(getString(R.string.item_code), item.itemCode))
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.item_desc),
                    item.itemDecription
                )
            )
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.del_qty),
                    item.delQty
                )
            )

            childList.add(innerCHildList)
        }
        adapter.notifyDataSetChanged()
        empty_list_item.setVisibility(View.GONE)



    }

    private fun generateEachCellItem(
        string: String,
        value: String
    ): KeyValue {

        val details = KeyValue(string, value)
        return details
    }
    private val groupDatabinder: FTADataBinder<String> =
        object : FTADataBinder<String>() {
            override fun bind(
                viewStatements: String,
                view: View,
                groupPosition: Int
            ) {
                findTextViewIDs(view)
                val lblListHeader = view
                    .findViewById<View>(R.id.descriptionText) as TextView
                lblListHeader.text = viewStatements
            }

            private fun findTextViewIDs(convertView: View) {}
        }
    private val childDatabinder: FTADataBinder<KeyValue> =
        object : FTADataBinder<KeyValue>() {
            override fun bind(
                viewStatements: KeyValue,
                view: View,
                groupPosition: Int
            ) {
                findTextViewIDs(view)
                val txtListChild = view
                    .findViewById<View>(R.id.descriptionText) as TextView
                val lblListAMount = view
                    .findViewById<View>(R.id.amount) as TextView
                txtListChild.setText(viewStatements.name)
                lblListAMount.setText(viewStatements.value)

                if (viewStatements.name.equals(
                        getString(R.string.cust_name),
                        true
                    )
                ) {
                    lblListAMount.setTextColor(context!!.resources!!.getColor(R.color.colorBlue))
                }
                else{
                    lblListAMount.setTextColor(context!!.resources!!.getColor(R.color.claim_text_color))

                }

                lblListAMount.setOnClickListener({
                    if (viewStatements.name.equals(getString(R.string.cust_name), true)) {
                        val bundle = bundleOf("tripNumber" to tripNumber,"customerName" to viewStatements.value)

                        findNavController().navigate(R.id.nav_trip_delivery_to_customer,bundle)

                    }
                })

            }

            private fun findTextViewIDs(convertView: View) {}
        }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
    fun basicAlert(message: String?){

        val builder = AlertDialog.Builder(context!!)

        with(builder)
        {
            setTitle("Alert")
            setMessage(message)
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton(android.R.string.no, negativeButtonClick)
// setNeutralButton("Maybe", neutralButtonClick)
            show()
        }


    }
    val positiveButtonClick = { dialog: DialogInterface, which: Int ->

        postDetails()

        dialog.dismiss()

    }
    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()

    }

    private fun postDetails() {
        var activity = activity as BaseActivity

        if (activity.isNetworkAvailable(activity)) {
            val msg: String = getResources().getString(R.string.msg_please_wait)
            ProgressHUD.show(activity, msg, true, false, null)

            val listNoteArray: ArrayList<DeliveryNoteDetailsArrayData?> = ArrayList()


                val details1
                        = DeliveryNoteDetailsArrayData(
                    profileName,"",0,"","",
                    "",0.0 , 0,
                    0,0,"","",
                    "","","",tripNumber!!.toInt(),"")

                listNoteArray.add(details1)



            val jsonParamsArrayData = JsonParamsArrayData(tripNumber as String)

            val scannedArrayData = ScannedArrayData(profileName,"","NoData","","",1,"")

            val signatureArrayData = SignatureArrayData(profileName,"","","","NoData","")

            val emptyscannedArrayData = EmptyscannedArrayData(profileName,"","NoData","","",1,"")

            val emptyscannedArrayDataLits: ArrayList<EmptyscannedArrayData?> =
                ArrayList()

            emptyscannedArrayDataLits.add(emptyscannedArrayData)
            val deliveryNoteDetails = DeliveryNoteDetailsData(listNoteArray, emptyscannedArrayDataLits, listOf(jsonParamsArrayData),
                listOf(scannedArrayData),
                listOf(signatureArrayData))

            disposable =
                wikiApiServe.postDeliveryNoteDetails(deliveryNoteDetails)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> closeResult(result) },
                        { error -> showErrorUpdating(error.message) }
                    )
        } else

            showErrorUpdating("Intenet connection unavailable")
    }

    private fun closeResult(result: String?) {

        var activity = activity as BaseActivity

        ProgressHUD.dismisss()
        popAlert("Details updated");


    }

    private fun showErrorUpdating(message: String?) {

        var activity = activity as BaseActivity

        ProgressHUD.dismisss()
        activity.basicAlert(message);


    }


    fun popAlert(message: String?){

        val builder = AlertDialog.Builder(context!!)

        with(builder)
        {
            setTitle("Alert")
            setMessage(message)
            setPositiveButton("OK", DialogInterface.OnClickListener(function = popButtonClick))
            setNegativeButton(android.R.string.no, negativeButtonClick)
// setNeutralButton("Maybe", neutralButtonClick)
            show()
        }


    }
    val popButtonClick = { dialog: DialogInterface, which: Int ->

        findNavController().popBackStack(R.id.nav_trip_details, true)
        dialog.dismiss()

    }


}