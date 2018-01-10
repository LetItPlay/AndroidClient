package com.letitplay.maugry.letitplay.user_flow.ui

import android.os.Bundle
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.widget.MusicPlayerSmall
import kotlinx.android.synthetic.main.navigation_main.*


class NavigationActivity : BaseActivity(R.layout.navigation_main) {

    val musicPlayerSmall: MusicPlayerSmall?
        get() = music_player_small

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
    }
}
