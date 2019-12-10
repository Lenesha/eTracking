package com.airtec.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airtec.R
import kotlinx.android.synthetic.main.signature_pad.*
import java.io.ByteArrayOutputStream

class SignatureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signature_pad)

        update_status.findViewById<TextView>(R.id.update).setText("Apply")
        update_status.findViewById<TextView>(R.id.update).setOnClickListener {
            if (signature_view.isBitmapEmpty) {
                Toast.makeText(this, "please make any signature", Toast.LENGTH_SHORT).show();
            } else {
                val bitmap = signature_view.signatureBitmap

                val data = Intent()
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)

                data.putExtra("BASE64", Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT))

                setResult(25,data)
                finish()

            }
        }


    }
}