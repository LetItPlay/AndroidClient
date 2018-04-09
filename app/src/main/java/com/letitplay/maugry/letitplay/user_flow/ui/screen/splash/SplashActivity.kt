package com.letitplay.maugry.letitplay.user_flow.ui.screen.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.utils.PreferenceHelper

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
            navigate()
        }
    }

    private fun handleDeeplinking(intent: Intent) {
        val data = intent.data
        val path = data.path
        val lastSegment = data.lastPathSegment
        // TODO: Parse query
        val isChannelLink = path.contains("station")
        val startIntent = Intent(this, NavigationActivity::class.java)
                .apply {
                    val key = if (isChannelLink) NavigationActivity.ARG_CHANNEL_ID else NavigationActivity.ARG_TRACK_ID
                    putExtra(key, lastSegment.toInt())
                }
        startActivity(startIntent)
    }

    private fun setDefaultLanguage() {
        prefHelper.contentLanguage = Language.EN
    }

    private fun navigate() {
        if (prefHelper.contentLanguage == null) {
            startActivity(Intent(this, LanguageActivity::class.java))
        } else {
            startActivity(Intent(this, NavigationActivity::class.java))
        }
    }
}