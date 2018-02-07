package com.letitplay.maugry.letitplay.user_flow.ui

import android.os.Bundle
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.widget.MusicPlayerSmall
import kotlinx.android.synthetic.main.navigation_main.*
import org.joda.time.DateTime
import timber.log.Timber


class NavigationActivity : BaseActivity(R.layout.navigation_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ACTIVITY"+ DateTime.now().toString())
        setSupportActionBar(toolbar)
    }
}
