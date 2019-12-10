package com.airtec.ui.tripstatus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airtec.R
import com.airtec.activities.BaseActivity
import com.airtec.customviews.ProgressHUD
import com.airtec.model.DeliveryNoteDetails
import com.airtec.network.NetworkInterface
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_tripstatus.*

class TripStatusFragment : Fragment() {

    private lateinit var toolsViewModel: TripStatusViewModel
    val wikiApiServe by lazy {
        NetworkInterface.create()
    }
    var disposable: Disposable? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        toolsViewModel =
                ViewModelProviders.of(this).get(TripStatusViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tripstatus, container, false)
//        val textView: TextView = root.findViewById(R.id.text_tools)
//        toolsViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        search!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                getTripStatus(query!!)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }

    private fun getTripStatus(tripNumber: String) {
        var activity = activity as BaseActivity

        if (activity.isNetworkAvailable(activity)) {
            val msg: String = getResources().getString(R.string.msg_please_wait)
            ProgressHUD.show(activity, msg, true, false, null)

            disposable =
                wikiApiServe.getTripStatus(tripNumber)
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
         activity.basicAlert("No items available");
        text_tools.text = "Trip Status:Unavailable"


    }

    private fun showResult(token: Any) {
        ProgressHUD.dismisss()

        text_tools.text = "Trip Status:"+token.toString()

    }

    override fun onPause() {
        super.onPause()

        disposable?.dispose()
    }
}