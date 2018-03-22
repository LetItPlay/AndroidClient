package com.letitplay.maugry.letitplay.user_flow.ui.screen.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.utils.PreferenceHelper


class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        navigate()
    }

    private fun navigate() {
        val prefHelper = PreferenceHelper(this)

        if (prefHelper.contentLanguage == null) {
            startActivity(Intent(this, LanguageActivity::class.java))
        } else {
            startActivity(Intent(this, NavigationActivity::class.java))
        }
    }
}