package com.letitplay.maugry.letitplay.user_flow.ui.screen.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import java.util.*


class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        navigate()
    }

    fun navigate() {
        val prefHelper = this.let { PreferenceHelper(it) }
        if (prefHelper.userToken == null) prefHelper.userToken = UUID.randomUUID().toString()
        if (prefHelper.contentLanguage == null) {
            this.startActivity(Intent(this, LanguageActivity::class.java))
        } else {
            this.startActivity(Intent(this, NavigationActivity::class.java))
        }
    }
}