package com.airtec.ui.logout

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.airtec.R
import com.airtec.activities.BaseActivity
import com.airtec.customviews.ProgressHUD

class LogoutFragment : Fragment() {

    private lateinit var shareViewModel: LogoutViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        shareViewModel =
                ViewModelProviders.of(this).get(LogoutViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_logout, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        basicAlert("Do you want to logout?")

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

        activity!!.finish()
        dialog.dismiss()

    }
    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()

    }
}