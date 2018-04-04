package com.letitplay.maugry.letitplay.user_flow.ui

import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.global.PlayerViewModel
import com.letitplay.maugry.letitplay.utils.ext.active
import com.zhuinden.simplestack.StateChange
import timber.log.Timber


class NavigationActivity : BaseActivity(R.layout.navigation_main) {

    private val playerVm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(PlayerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val trackIdToPlay = intent.getIntExtra(ARG_TRACK_ID, UNDEFINED_ID)
        val channelIdToShow = intent.getIntExtra(ARG_CHANNEL_ID, UNDEFINED_ID)
        when {
            trackIdToPlay != UNDEFINED_ID -> {
                Timber.d("Intent to play track $trackIdToPlay")
                playerVm.fetchAndPlay(trackIdToPlay)
            }
            channelIdToShow != UNDEFINED_ID -> {
                Timber.d("Intent to channel page $channelIdToShow")
                navigationMenu?.active(R.id.action_channels)
                backstackDelegate.backstack.setHistory(listOf(ChannelsKey(), ChannelPageKey(channelIdToShow)), StateChange.REPLACE)
            }
        }
    }

    companion object {
        const val ARG_TRACK_ID = "ARG_TRACK_ID"
        const val ARG_CHANNEL_ID = "ARG_CHANNEL_ID"
        const val UNDEFINED_ID = -1
    }
}
