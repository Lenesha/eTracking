package com.airtec.ui.tripcustomers

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airtec.R
import com.airtec.activities.BaseActivity
import com.airtec.activities.SignatureActivity
import com.airtec.customviews.ProgressHUD
import com.airtec.model.DeliveryNoteDetails
import com.airtec.model.KeyValue
import com.airtec.model.postdelivery.*
import com.airtec.network.NetworkInterface
import com.airtec.ui.adapter.FTADataBinder
import com.airtec.ui.adapter.GenericExpandableListAdaptor
import com.google.gson.GsonBuilder

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_customerdelivery.*
import me.sudar.zxingorient.ZxingOrient
import me.sudar.zxingorient.ZxingOrientResult
import kotlin.collections.ArrayList


class DeliveryToCustomerFragment() : Fragment() {

    val emptyscannedArrayData: ArrayList<ScannedArrayData?> =
        ArrayList()
    var emptyCylinderCount = 1

    var scannedResult: String = ""


    var detailsFooter: View? = null
    var nextFooter: View? = null




    private lateinit var adapter: GenericExpandableListAdaptor<String, KeyValue>
    val wikiApiServe by lazy {
        NetworkInterface.create()
    }
    var disposable: Disposable? = null

    private var tripNumber: String? = null
    private var customerName: String? = null

    private var profileName: String? = ""

    private lateinit var Model: List<DeliveryNoteDetails>
    var medicalBenefitsGroup =
        ArrayList<String>()
    var childList: ArrayList<ArrayList<KeyValue>> =
        ArrayList<ArrayList<KeyValue>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_customerdelivery, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profileName = activity!!.intent.extras!!.getString("USER")

        tripNumber = arguments?.getString("tripNumber")
        customerName = arguments?.getString("customerName")

        search.text = "Delivery to Customer: " + customerName + "(" + tripNumber + ")"
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


        val footer = View.inflate(activity, R.layout.update_status, null)

        // Add the footer before the setAdapter() method
        // Add the footer before the setAdapter() method
        expandableListView.addFooterView(footer)

        expandableListView.setAdapter(adapter)

        getdeliveryDetails(tripNumber as String)

        footer.findViewById<TextView>(R.id.update).setText("Delivery to customer scan")

        footer.findViewById<TextView>(R.id.update).setOnClickListener({

            run {

                val orient = ZxingOrient(this).setBeep(true)
                orient.addExtra("RCODE", 1)
                orient.initiateScan()
            }
        })

//        if(emptyscannedArrayData!=null && emptyscannedArrayData.size>0){
//
//            for(item in emptyscannedArrayData){
//                var  detailsFooter = layoutInflater.inflate(
//                    R.layout.scanned_result,
//                    expandableListView,
//                    false
//                )
//
//
//                detailsFooter!!.findViewById<View>(R.id.la1)
//                    .findViewById<TextView>(R.id.descriptionText)
//                    .setText("SL NO")
//                detailsFooter!!.findViewById<View>(R.id.la1)
//                    .findViewById<TextView>(R.id.amount).setText(emptyCylinderCount.toString())
//                emptyCylinderCount++
//
//                detailsFooter!!.findViewById<View>(R.id.la2)
//                    .findViewById<TextView>(R.id.descriptionText)
//                    .setText(getString(R.string.tripNumber))
//                detailsFooter!!.findViewById<View>(R.id.la2)
//                    .findViewById<TextView>(R.id.amount)
//                    .setText(tripNumber)
//
//                detailsFooter!!.findViewById<View>(R.id.la3)
//                    .findViewById<TextView>(R.id.descriptionText)
//                    .setText("Barcode Value")
//                detailsFooter!!.findViewById<View>(R.id.la3)
//                    .findViewById<TextView>(R.id.amount)
//                    .setText(scannedResult)
//
//                detailsFooter!!.findViewById<ImageView>(R.id.delete).setOnClickListener({
//
//                    expandableListView.removeFooterView(detailsFooter)
//
//                })
//                detailsFooter!!.findViewById<View>(R.id.update_status).visibility = GONE
//
//                expandableListView.addFooterView(detailsFooter)
//            }
//
//            addNextFooter()
//
//
//        }


    }

    private fun getdeliveryDetails(tripNumber: String) {
        var activity = activity as BaseActivity

        if (activity.isNetworkAvailable(activity)) {
            val msg: String = getResources().getString(R.string.msg_please_wait)
            ProgressHUD.show(activity, msg, true, false, null)

            disposable =
                wikiApiServe.getDeliveryDetails(tripNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> showResult(result) },
                        { error -> showError(error.message) }
                    )
        } else

            showErrorUpdating("Intenet connection unavailable")
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
        Model = gson.fromJson(token as String, Array<DeliveryNoteDetails>::class.java).toList()
        if (Model.size == 0)
            showEmptyView(getString(R.string.no_items))
        else
            populateGroupAndChild(Model)
    }

    private fun showEmptyView(string: String) {
        view!!.findViewById<View>(R.id.empty_list_item).setVisibility(View.VISIBLE)
        empty_list_item.text = string
        medicalBenefitsGroup.clear()
        childList.clear()
        adapter.notifyDataSetChanged()
    }

    private fun populateGroupAndChild(branchList: List<DeliveryNoteDetails>) {
        medicalBenefitsGroup.clear()
        childList.clear()
        for (item in branchList) {
            if (TextUtils.isEmpty(item.tripNumber)) {

                break
            }
            val innerCHildList: ArrayList<KeyValue> =
                ArrayList<KeyValue>()
            medicalBenefitsGroup.add(item.tripNumber!!)
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.tripNumber),
                    item.tripNumber!!
                )
            )

            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.cust_acc_num),
                    item.custAccountNumber!!
                )
            )
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.cust_acc_id),
                    item.custAccountID!!
                )
            )
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.cust_name),
                    item.custName!!
                )
            )
            innerCHildList.add(generateEachCellItem(getString(R.string.item_code), item.itemCode!!))
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.item_desc),
                    item.itemDecription!!
                )
            )
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.del_qty),
                    item.delQty!!
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
            }

            private fun findTextViewIDs(convertView: View) {}
        }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    fun basicAlert(message: String?) {

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

    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var result: ZxingOrientResult? =
            ZxingOrient.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {

            if (result.contents != null) {

                scannedResult = result.contents
                val actu = activity as BaseActivity
                actu.basicAlert("Scan success: \nScanned Code:" + scannedResult)
                detailsFooter =
                    layoutInflater.inflate(
                        R.layout.scanned_result,
                        expandableListView,
                        false
                    )
//                if (emptyCylinderCount == 1) {
//                    detailsFooter!!.findViewById<View>(R.id.detail_header).visibility = VISIBLE
//                } else
//                    detailsFooter!!.findViewById<View>(R.id.detail_header).visibility = GONE


                val empty = ScannedArrayData(
                    profileName,
                    "",
                    scannedResult,
                    "",
                    "",
                    emptyCylinderCount,
                    tripNumber
                )

                emptyscannedArrayData.add(empty)

                detailsFooter!!.findViewById<View>(R.id.la1)
                    .findViewById<TextView>(R.id.descriptionText)
                    .setText("SL NO")
                detailsFooter!!.findViewById<View>(R.id.la1)
                    .findViewById<TextView>(R.id.amount).setText(emptyCylinderCount.toString())
                emptyCylinderCount++

                detailsFooter!!.findViewById<View>(R.id.la2)
                    .findViewById<TextView>(R.id.descriptionText)
                    .setText(getString(R.string.tripNumber))
                detailsFooter!!.findViewById<View>(R.id.la2)
                    .findViewById<TextView>(R.id.amount)
                    .setText(tripNumber)

                detailsFooter!!.findViewById<View>(R.id.la3)
                    .findViewById<TextView>(R.id.descriptionText)
                    .setText("Barcode Value")
                detailsFooter!!.findViewById<View>(R.id.la3)
                    .findViewById<TextView>(R.id.amount)
                    .setText(scannedResult)
                detailsFooter!!.setTag(empty)

                detailsFooter!!.findViewById<ImageView>(R.id.delete).setOnClickListener({

                    val empty  =   detailsFooter!!.tag
                    expandableListView.removeFooterView(detailsFooter)

                    emptyscannedArrayData.remove(empty)

                    if(emptyscannedArrayData.size ==0)

                        if(nextFooter!=null)
                            expandableListView.removeFooterView(nextFooter)
                })
                detailsFooter!!.findViewById<View>(R.id.update_status).visibility = GONE


                expandableListView.addFooterView(detailsFooter)

                addNextFooter()


            } else {
                var activity = activity as BaseActivity

                activity.basicAlert("scan failed")
            }
        }
            else {
                super.onActivityResult(requestCode, resultCode, data)
            }


        }

    private fun addNextFooter() {
        if(nextFooter!=null)
            expandableListView.removeFooterView(nextFooter)
        nextFooter =
            layoutInflater.inflate(
                R.layout.update_status,
                expandableListView,
                false
            )
        nextFooter!!.findViewById<TextView>(R.id.update).setText("Next")
        nextFooter!!
            .findViewById<TextView>(R.id.update).setOnClickListener({

                basicAlert("Scanning done?")


            })

        expandableListView.addFooterView(nextFooter)
    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->


            val bundle = bundleOf(
                "tripNumber" to tripNumber,
                "customerName" to customerName,
                "Model" to Model,
                "SCANNED" to emptyscannedArrayData
            )


            findNavController().navigate(R.id.nav_trip_customer_sign_scanempty, bundle)


        }


        private fun showErrorUpdating(message: String?) {

            var activity = activity as BaseActivity

            ProgressHUD.dismisss()
            activity.basicAlert(message);


        }


    }
