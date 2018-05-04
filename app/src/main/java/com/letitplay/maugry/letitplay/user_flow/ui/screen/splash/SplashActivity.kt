package com.letitplay.maugry.letitplay.user_flow.ui.screen.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.ext.getUrlParams
import java.util.*

class SplashActivity : AppCompatActivity() {

    private val prefHelper by lazy { PreferenceHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        val intent = intent
        if (intent.data != null) {
            if (prefHelper.contentLanguage == null) {
                setDefaultLanguage()
            }
            handleDeeplinking(intent)
        } else {
            authorization()
        }
    }

    private fun handleDeeplinking(intent: Intent) {
        val data = intent.dataString
        val urlParams = data.getUrlParams()
        val isTracklLink = data.contains("track")
        val startIntent = Intent(this, NavigationActivity::class.java)
                .apply {
                    putExtra(NavigationActivity.ARG_CHANNEL_ID, urlParams["channel"])
                    if (isTracklLink) putExtra(NavigationActivity.ARG_TRACK_ID, urlParams["track"])
                }
        startActivity(startIntent)
    }

    private fun setDefaultLanguage() {
        prefHelper.contentLanguage = Language.EN
    }

    private fun authorization() {
        val prefHelper = this.let { PreferenceHelper(it) }
        if (prefHelper.userToken == null) prefHelper.userToken = UUID.randomUUID().toString()

        if (prefHelper.userJwt.isEmpty()) {
            ServiceLocator.profileRepository
                    .signUp(prefHelper.userToken!!, "User")
                    .doOnSuccess {
                        prefHelper.userJwt = it.headers().get("Authorization")
                    }
                    .doFinally { navigate() }
                    .subscribe()
        } else {
            ServiceLocator.profileRepository
                    .signIn(prefHelper.userToken!!, "User")
                    .doOnSuccess {
                        prefHelper.userJwt = it.headers().get("Authorization")
                    }
                    .doFinally { navigate() }
                    .subscribe()
        }
    }

    private fun navigate() {
        startActivity(Intent(this, NavigationActivity::class.java))
        prefHelper.contentLanguage = Language.fromString(Locale.getDefault().language) ?: Language.EN
    }
}