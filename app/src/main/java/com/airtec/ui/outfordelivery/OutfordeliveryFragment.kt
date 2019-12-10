package com.airtec.ui.outfordelivery

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airtec.R
import com.airtec.activities.BaseActivity
import com.airtec.customviews.ProgressHUD
import com.airtec.model.DeliveryNoteDetails
import com.airtec.model.KeyValue
import com.airtec.network.NetworkInterface
import com.airtec.ui.adapter.FTADataBinder
import com.airtec.ui.adapter.GenericExpandableListAdaptor
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_outfordelivery.*
import java.util.*

class OutfordeliveryFragment : Fragment() {

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

        val root = inflater.inflate(R.layout.fragment_outfordelivery, container, false)

        return root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var tripNumber = arguments?.getString("tripNumber")
        search.text = "Loading Details: "+tripNumber
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


        val footer = layoutInflater.inflate(R.layout.update_status, expandableListView, false)
        // Add the footer before the setAdapter() method
        // Add the footer before the setAdapter() method
        expandableListView.addFooterView(footer)

        expandableListView.setAdapter(adapter)

        getdeliveryDetails(tripNumber!!)


        footer.findViewById<TextView>(R.id.update).setOnClickListener({
            basicAlert("Do you want to update the status")

        })
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
        val Model = gson.fromJson(token as String, Array<DeliveryNoteDetails>::class.java).toList()
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
            val innerCHildList: ArrayList<KeyValue> =
                ArrayList<KeyValue>()
            medicalBenefitsGroup.add(item.tripNumber!!)
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.tripNumber),
                    item.tripNumber
                )
            )

            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.cust_acc_num),
                    item.custAccountNumber!!
                )
            )
            innerCHildList.add(generateEachCellItem(getString(R.string.cust_acc_id), item.custAccountID!!))
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
            override fun bind(viewStatements: String?, view: View) {
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
                view: View
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
    fun basicAlert(message: String?){

        val builder = AlertDialog.Builder(context!!)

        with(builder)
        {
            setTitle("Alert")
            setMessage(message)
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton(android.R.string.no, negativeButtonClick)
//            setNeutralButton("Maybe", neutralButtonClick)
            show()
        }


    }
    val positiveButtonClick = { dialog: DialogInterface, which: Int ->

        findNavController().navigate(R.id.nav_trip_scan)
        dialog.dismiss()

    }
    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()

    }
}