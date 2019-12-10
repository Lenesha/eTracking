package com.airtec.ui.tripcustomers

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airtec.R
import com.airtec.activities.BaseActivity
import com.airtec.activities.MainActivity
import com.airtec.activities.SignatureActivity
import com.airtec.customviews.ProgressHUD
import com.airtec.model.DeliveryNoteDetails
import com.airtec.model.KeyValue
import com.airtec.model.postdelivery.*
import com.airtec.network.NetworkInterface
import com.airtec.ui.adapter.FTADataBinder
import com.airtec.ui.adapter.GenericExpandableListAdaptor
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.kyanogen.signatureview.SignatureView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_loading.*
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList


class DeliveryToCustomerFragment() : Fragment() {
    private var base64Signature: String? = ""

    val emptyscannedArrayData: ArrayList<EmptyscannedArrayData?> =
        ArrayList()

    var scannedResult: String = ""

    var EmptyscannedResult: String = ""

    var detailsFooter :View?=null

    var emptyCylinderCount = 1

    var emptyCylinderFooter :View?=null

    var signaturefooter :View?=null

    private lateinit var adapter: GenericExpandableListAdaptor<String, KeyValue>
    val wikiApiServe by lazy {
        NetworkInterface.create()
    }
    var disposable: Disposable? = null

    private var tripNumber: String? = null

    private var profileName:String? =""

    private lateinit var Model :List<DeliveryNoteDetails>
    var medicalBenefitsGroup =
        ArrayList<String>()
    var childList: ArrayList<ArrayList<KeyValue>> =
        ArrayList<ArrayList<KeyValue>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_loading, container, false)

        return root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

         profileName = activity!!.intent.extras!!.getString("USER")

        tripNumber = arguments?.getString("tripNumber")
        val customerName = arguments?.getString("customerName")

        search.text = "Delivery to Customer: "+customerName+"("+tripNumber+")"
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
                IntentIntegrator.forSupportFragment(this).setOrientationLocked(true).setRequestCode(1).initiateScan();
            }
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
            if(TextUtils.isEmpty(item.tripNumber )){

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
// setNeutralButton("Maybe", neutralButtonClick)
            show()
        }


    }

    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode ==25){
            signaturefooter!!.findViewById<View>(R.id.update_status).visibility = VISIBLE;
            signaturefooter!!.findViewById<View>(R.id.sign_image).visibility = GONE;
             base64Signature =  data!!.getStringExtra("BASE64")

            val   decodedString = Base64.decode(base64Signature, Base64.DEFAULT);

            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size);


            signaturefooter!!.findViewById<AppCompatImageView>(R.id.img_signature).setImageBitmap(decodedByte)
            signaturefooter!!.findViewById<AppCompatImageView>(R.id.img_signature).visibility = VISIBLE
            signaturefooter!!.findViewById<View>(R.id.update_status).findViewById<TextView>(R.id.update).setText("Update Status")
            signaturefooter!!.findViewById<View>(R.id.update_status).findViewById<TextView>(R.id.update).setOnClickListener({


                basicPostAlert("Item checked and scanned.Do you want to update the status?")

            })

        }
        else{
            var result: IntentResult? =
                IntentIntegrator.parseActivityResult( resultCode, data)

            if (result != null) {

                if (result.contents != null) {

                    if(requestCode ==1){

                        scannedResult = result.contents
                        val actu =  activity as BaseActivity
                        actu.basicAlert("Scan success: \nScanned Code:" + scannedResult)
                        detailsFooter =
                            layoutInflater.inflate(R.layout.scanned_result, expandableListView, false)


                        detailsFooter!!.findViewById<View>(R.id.la1).findViewById<TextView>(R.id.descriptionText)
                            .setText("SL NO")
                        detailsFooter!!.findViewById<View>(R.id.la1).findViewById<TextView>(R.id.amount).setText("1")

                        detailsFooter!!.findViewById<View>(R.id.la2).findViewById<TextView>(R.id.descriptionText)
                            .setText(getString(R.string.tripNumber))
                        detailsFooter!!.findViewById<View>(R.id.la2).findViewById<TextView>(R.id.amount)
                            .setText(tripNumber)

                        detailsFooter!!.findViewById<View>(R.id.la3).findViewById<TextView>(R.id.descriptionText)
                            .setText("Barcode Value")
                        detailsFooter!!.findViewById<View>(R.id.la3).findViewById<TextView>(R.id.amount)
                            .setText(scannedResult)

                        detailsFooter!!.findViewById<ImageView>(R.id.delete).setOnClickListener({

                            expandableListView.removeFooterView(detailsFooter)

                        })
                        detailsFooter!!.findViewById<View>(R.id.update_status).findViewById<TextView>(R.id.update).setText("Next")
                        detailsFooter!!.findViewById<View>(R.id.update_status).findViewById<TextView>(R.id.update).setOnClickListener({

                            basicAlert("Scanning done?")


                        })

                        expandableListView.addFooterView(detailsFooter)
                    }
                    else if(requestCode ==2){

                        EmptyscannedResult = result.contents
                        val actu =  activity as BaseActivity

                        actu.basicAlert("Scan success: \nScanned Code:" + EmptyscannedResult)
                        emptyCylinderFooter =
                            layoutInflater.inflate(R.layout.scanned_result, expandableListView, false)

                        if(emptyCylinderCount ==1)
                        {
                            emptyCylinderFooter!!.findViewById<View>(R.id.detail_header).visibility= VISIBLE
                        }
                        else
                            emptyCylinderFooter!!.findViewById<View>(R.id.detail_header).visibility = GONE


                        val empty = EmptyscannedArrayData(profileName,"",EmptyscannedResult,"","",emptyCylinderCount,tripNumber)

                        emptyscannedArrayData.add(empty)
                        emptyCylinderFooter!!.findViewById<View>(R.id.la1).findViewById<TextView>(R.id.descriptionText)
                            .setText("SL NO")
                        emptyCylinderFooter!!.findViewById<View>(R.id.la1).findViewById<TextView>(R.id.amount).setText(emptyCylinderCount.toString())

                        emptyCylinderCount++

                        emptyCylinderFooter!!.findViewById<View>(R.id.la2).findViewById<TextView>(R.id.descriptionText)
                            .setText(getString(R.string.tripNumber))
                        emptyCylinderFooter!!.findViewById<View>(R.id.la2).findViewById<TextView>(R.id.amount)
                            .setText(tripNumber)

                        emptyCylinderFooter!!.findViewById<View>(R.id.la3).findViewById<TextView>(R.id.descriptionText)
                            .setText("Barcode Value")
                        emptyCylinderFooter!!.findViewById<View>(R.id.la3).findViewById<TextView>(R.id.amount)
                            .setText(EmptyscannedResult)

                        emptyCylinderFooter!!.findViewById<ImageView>(R.id.delete).setOnClickListener({

                            expandableListView.removeFooterView(emptyCylinderFooter)

                        })

                        emptyCylinderFooter!!.findViewById<View>(R.id.update_status).findViewById<TextView>(R.id.update).visibility = GONE

                        expandableListView.addFooterView(emptyCylinderFooter)
                    }


                } else {
                    var activity = activity as BaseActivity

                    activity.basicAlert("scan failed")
                }
            }
            else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }

    }
    val positiveButtonClick = { dialog: DialogInterface, which: Int ->


        //show scan empty cylinders and notepad for sign
        detailsFooter!!.findViewById<View>(R.id.update_status).visibility = GONE

        detailsFooter!!.findViewById<View>(R.id.empty_cylinders).visibility = VISIBLE

        detailsFooter!!.findViewById<View>(R.id.empty_cylinders).findViewById<TextView>(R.id.update).setText("Scan Empty Cylinders")
        detailsFooter!!.findViewById<View>(R.id.empty_cylinders).findViewById<TextView>(R.id.update).setOnClickListener({

            run {
                IntentIntegrator.forSupportFragment(this).setOrientationLocked(true).setRequestCode(2).initiateScan();


            }


        })
        dialog.dismiss()

         signaturefooter =
            layoutInflater.inflate(R.layout.signature_imageview, expandableListView, false)

        signaturefooter!!.findViewById<View>(R.id.sign_image).findViewById<TextView>(R.id.update).setText("Sign")

        signaturefooter!!.findViewById<View>(R.id.sign_image).findViewById<TextView>(R.id.update).setOnClickListener({


            val intent =   Intent(activity, SignatureActivity::class.java)

            startActivityForResult(intent,25)







        })

        expandableListView.addFooterView(signaturefooter)

    }

    fun basicPostAlert(message: String?){

        val builder = AlertDialog.Builder(context!!)

        with(builder)
        {
            setTitle("Alert")
            setMessage(message)
            setPositiveButton("OK", DialogInterface.OnClickListener(function = PostpositiveButtonClick))
            setNegativeButton(android.R.string.no, negativeButtonClick)
// setNeutralButton("Maybe", neutralButtonClick)
            show()
        }


    }
    val PostpositiveButtonClick = { dialog: DialogInterface, which: Int ->

        postDetails()

        dialog.dismiss()

    }

    private fun postDetails() {
        var activity = activity as BaseActivity

        if (activity.isNetworkAvailable(activity)) {
            val msg: String = getResources().getString(R.string.msg_please_wait)
            ProgressHUD.show(activity, msg, true, false, null)

            val listNoteArray: ArrayList<DeliveryNoteDetailsArrayData?> =
                ArrayList()

            for(item in Model ){

                val details1
                        = DeliveryNoteDetailsArrayData(
                    profileName,"",0,"","",
                    "",0.0 , 0,
                    0,0,item.itemCode,item.itemDecription,
                    "","","",tripNumber!!.toInt(),""
                )
                listNoteArray.add(details1)

            }

            val jsonParamsArrayData = JsonParamsArrayData(tripNumber as String)

            val scannedArrayData = ScannedArrayData(profileName,"",scannedResult,"","",1,tripNumber)

            val signatureArrayData = SignatureArrayData(profileName,"","","",base64Signature,tripNumber)

            val empTYData = EmptyscannedArrayData(profileName,"","","","",1,tripNumber)
            if(emptyscannedArrayData!=null && emptyscannedArrayData.size ==0)
            emptyscannedArrayData.add(empTYData)

            val deliveryNoteDetails = DeliveryNoteDetailsData(listNoteArray, emptyscannedArrayData, listOf(jsonParamsArrayData),
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
