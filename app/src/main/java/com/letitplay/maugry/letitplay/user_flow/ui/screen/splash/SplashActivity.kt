package com.letitplay.maugry.letitplay.user_flow.ui.screen.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit


class SplashActivity : AppCompatActivity() {

    private var navigatePromise: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        Observable.just(Any())
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribeBy(onComplete = { navigate() })

    }

    fun navigate() {
        val prefHelper = this.let { PreferenceHelper(it) }
        if (prefHelper.contentLanguage == ContentLanguage.UNKNOWN) {
            this.startActivity(Intent(this, LanguageActivity::class.java))
        } else {
            this.startActivity(Intent(this, NavigationActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        navigatePromise?.dispose()
    }
}