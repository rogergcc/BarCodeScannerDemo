package com.rogergcc.barcodescannerdemo.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.rogergcc.barcodescannerdemo.MainActivity
import com.rogergcc.barcodescannerdemo.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private var topAnimation: Animation? = null
    private var bottomAnimation: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash)
        binding = ActivitySplashBinding.inflate(layoutInflater)

        val view: View = binding.root
        setContentView(view)
        topAnimation =
            AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_slide_in_bottom);
        bottomAnimation =
            AnimationUtils.loadAnimation(this, androidx.appcompat.R.anim.abc_slide_in_top);

        binding.imgLogo.animation = topAnimation;
//
        val intentGetStarted = Intent(this@SplashActivity, MainActivity::class.java).apply {
            startActivity(this)
        }
        finish()
    }

    private fun init() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}


