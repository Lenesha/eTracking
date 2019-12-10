package com.airtec.activities

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.airtec.R
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
     val    anim = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.fade_in
        ) // Create the animation.

        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        logo.startAnimation(anim)
    }
}