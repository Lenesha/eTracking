package com.airtec.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airtec.R
import com.airtec.customviews.ProgressHUD
import com.airtec.network.NetworkInterface
import com.airtec.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity:BaseActivity() {

    val wikiApiServe by lazy {
        NetworkInterface.create()
    }
    var disposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username.editText!!.setText("driver1")
        username.editText!!.setSelection(username.editText!!.length())
        password.editText!!.setText("123")

        loginBtn.setOnClickListener({v->
            if(validateUsername())beginSearch(username.editText!!.text.toString().trim(),password.editText!!.text.toString().trim())})
    }

    private  fun validateUsername(): Boolean {

        var isValid = true;
        if(username.editText!!.getText().toString().isEmpty()){
            username.editText!!.setError("Please enter username");
            isValid = false;
        }else{
            username.editText!!.setError(null);
            isValid = true;
        }


        if(password.editText!!.getText().toString().isEmpty()){
            password.editText!!.setError("Please enter Password");
            isValid = false;

        }else{
            password.editText!!.setError(null);
            isValid = true;
        }
        return isValid
    }

    private fun beginSearch(username: String, password: String) {

        if(isNetworkAvailable(this)){
            val msg: String = getResources().getString(R.string.msg_please_wait)
            ProgressHUD.show(this, msg, true, false, null)

            disposable =
                wikiApiServe.login(username, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> showResult(result) },
                        { error -> showError(error.message) }
                    )
        }else

            showError("Intenet connection unavailable")
    }

    private fun showError(message: String?) {


        ProgressHUD.dismisss()
        basicAlert(message);
    }

    private fun showResult(token: Any) {
        ProgressHUD.dismisss()
        val intent =   Intent(this, MainActivity::class.java)
        intent.putExtra("USER",username.editText!!.text.toString().trim())

        startActivity(intent)

    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }


}