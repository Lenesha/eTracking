package com.airtec.ui.tripdetails

import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.airtec.R
import com.airtec.activities.BaseActivity
import com.airtec.customviews.ProgressHUD
import com.airtec.model.KeyValue
import com.airtec.model.TripDetail
import com.airtec.network.NetworkInterface
import com.airtec.ui.adapter.FTADataBinder
import com.airtec.ui.adapter.GenericExpandableListAdaptor
import com.airtec.utils.DialogUtil
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_trip_detail.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TripDetailsFragment : Fragment() {
    private lateinit var mTripDate: String
    private var date: String=""

    val STATEMENT_DATE_FORMAT = "yyyy-MM-dd"
    private lateinit var Model :List<TripDetail>

    private lateinit var adapter: GenericExpandableListAdaptor<String, KeyValue>
    private lateinit var galleryViewModel: TripDetailViewModel

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
        galleryViewModel =
            ViewModelProviders.of(this).get(TripDetailViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_trip_detail, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val format = SimpleDateFormat(
            STATEMENT_DATE_FORMAT, Locale.ENGLISH
        )

        val cal = Calendar.getInstance()

        val mToDay = format.format(cal.time) //today

        if(TextUtils.isEmpty(date))
        date = mToDay

        date_picker.text = date

        val profileName = activity!!.intent.extras!!.getString("USER")

        var roleId = "3"
        if (profileName!!.equals("driver1", true))
            roleId = "3"
        else
            roleId = "6"

        view!!.findViewById<View>(R.id.search).setOnClickListener({ v ->
            getTripDetails(date_picker.text.toString(), roleId)

        })

        date_pickerLayout.setOnClickListener({
            openFromDatePicker()
        })

        reset.setOnClickListener({
            date_picker.text = mToDay
            getTripDetails(date_picker.text.toString(), roleId)


        })
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

        getTripDetails(date_picker.text.toString(), roleId)

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

                if (viewStatements.name.equals(getString(R.string.tripDate), true) || viewStatements.name.equals(getString(R.string.tripNumber), true)) {
                    lblListAMount.setTextColor(context!!.resources!!.getColor(R.color.colorBlue))
                }
                else{
                    lblListAMount.setTextColor(context!!.resources!!.getColor(R.color.claim_text_color))

                }
                lblListAMount.setOnClickListener({
                    if (viewStatements.name.equals(getString(R.string.tripDate), true)) {

                        var mdoel = Model.get(groupPosition)  //chaneg this logic

                        var bundle = bundleOf("tripNumber" to mdoel.tripNumber)

                        findNavController().navigate(R.id.nav_trip_customer,bundle)

                    } else if (viewStatements.name.equals(getString(R.string.tripNumber), true)  ) {

                        var mdoel = Model.get(groupPosition)
                        var bundle = bundleOf("tripNumber" to viewStatements.value)

                        if(mdoel.tripStatus.equals("OPEN",true) ||(mdoel.tripStatus.equals("Security Checked-In",true) )
                            || (mdoel.tripStatus.equals("Closed",true) ))  {

                          findNavController().navigate(R.id.nav_trip_loading, bundle)
                      }
                        else if(mdoel.tripStatus.equals("Loaded",true) || (mdoel.tripStatus.equals("Security Checked-Out",true)) ||
                            (mdoel.tripStatus.equals("Return Back",true)) || (mdoel.tripStatus.equals("'Delivered to Customer",true)))
                            {
                            findNavController().navigate(R.id.nav_trip_outfordelivery, bundle)
                            }


                    }
                })
            }

            private fun findTextViewIDs(convertView: View) {}
        }

    private fun showEmptyView(string: String) {
        view!!.findViewById<View>(R.id.empty_list_item).setVisibility(View.VISIBLE)
        empty_list_item.text = string
        medicalBenefitsGroup.clear()
        childList.clear()
        adapter.notifyDataSetChanged()
    }

    private fun populateGroupAndChild(branchList: List<TripDetail>) {
        medicalBenefitsGroup.clear()
        childList.clear()
        for (item in branchList) {
            val innerCHildList: ArrayList<KeyValue> =
                ArrayList<KeyValue>()
            medicalBenefitsGroup.add(item.tripNumber)
            try {
                innerCHildList.add(
                    generateEachCellItem(
                        getString(R.string.tid),
                        item.tID
                    )
                )

            } catch (e: ParseException) {
            }
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.tripNumber),
                    item.tripNumber
                )
            )
            innerCHildList.add(generateEachCellItem(getString(R.string.tripDate), item.tripDate))
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.menu_trip_status),
                    item.tripStatus
                )
            )
            innerCHildList.add(generateEachCellItem(getString(R.string.vehicle_id), item.vehID))
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.vehicle_number),
                    item.vehNumber
                )
            )
            innerCHildList.add(generateEachCellItem(getString(R.string.driver_id), item.driverID))
            innerCHildList.add(
                generateEachCellItem(
                    getString(R.string.driver_name),
                    item.driverName
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

    private fun getTripDetails(username: String, roleId: String) {
        var activity = activity as BaseActivity

        if (activity.isNetworkAvailable(activity)) {
            val msg: String = getResources().getString(R.string.msg_please_wait)
            ProgressHUD.show(activity, msg, true, false, null)

            disposable =
                wikiApiServe.getTripDetails(username, roleId)
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
         Model = gson.fromJson(token as String, Array<TripDetail>::class.java).toList()
        if (Model.size == 0)
            showEmptyView(getString(R.string.no_items))
        else
            populateGroupAndChild(Model)
    }

    private fun openFromDatePicker() {
        DialogUtil.showDatePickerDialog(activity,
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                mTripDate = String.format(
                    "%02d/%02d/%d",
                    dayOfMonth,
                    (monthOfYear + 1),
                    year
                )
                try {
                    mTripDate = DialogUtil.getMonth(mTripDate, "dd/MM/yyyy", "yyyy-MM-dd")
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                date = mTripDate
                date_picker.setText(mTripDate)
            })
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}