package com.letitplay.maugry.letitplay.user_flow.ui.screen.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.letitplay.maugry.letitplay.R


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.splash_fragment_container, SplashFragment())
                .commit()
    }
}