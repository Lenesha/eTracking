package com.airtec.activities

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.airtec.R

open class BaseActivity:AppCompatActivity() {

    fun basicAlert(message: String?){

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("Alert")
            setMessage(message)
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
//            setNegativeButton(android.R.string.no, negativeButtonClick)
//            setNeutralButton("Maybe", neutralButtonClick)
            show()
        }


    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->

        dialog.dismiss()

    }
    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()

    }
    val neutralButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
            "Maybe", Toast.LENGTH_SHORT).show()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    open fun replace(
        fragment: Fragment,
        containerId: Int,
        addToStack: Boolean,
        animationRequired: Boolean,
        tag: String?
    ) {
        try {
            val fragmentTransaction: FragmentTransaction =
                supportFragmentManager.beginTransaction()
            if (animationRequired) //                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
            if (addToStack) {
                fragmentTransaction.replace(containerId, fragment, tag).addToBackStack(tag).commit()
            } else {
                fragmentTransaction.replace(containerId, fragment).commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}