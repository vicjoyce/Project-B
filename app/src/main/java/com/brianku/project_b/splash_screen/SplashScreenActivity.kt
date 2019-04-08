package com.brianku.project_b.splash_screen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.brianku.project_b.R
import com.brianku.project_b.user_login_and_register.LoginScreenActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // before the content view being load, we set the splash to fullscreen
        makeFullScreen()
        setContentView(R.layout.activity_splash_screen)

        // using a handler to delay loading main activity

        Handler().postDelayed({
            startActivity(Intent(this,LoginScreenActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        },2000)

    }

    private fun makeFullScreen(){
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // make full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // hide the toolbar

        supportActionBar?.hide()
    }
}
