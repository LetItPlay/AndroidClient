package com.letitplay.maugry.letitplay.user_flow.ui

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.MenuItem
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists.PlaylistsKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.profile.ProfileKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.trends.TrendsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.FragmentStateChanger
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.HistoryBuilder
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger

object Navigator {

    lateinit var backstackDelegate: BackstackDelegate
    lateinit var fragmentStateChanger: FragmentStateChanger
    lateinit var activityCallback: (BaseKey) -> Unit

    fun setupBackstackDelegate(savedInstanceState: Bundle?, lastCustomNonConfigurationInstance: Any?,
                               supportFragmentManager: FragmentManager, activity: Activity, callback: (BaseKey) -> Unit) {
        this.activityCallback = callback
        backstackDelegate = BackstackDelegate(null)
        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.fragment_container)
        backstackDelegate.onCreate(savedInstanceState, lastCustomNonConfigurationInstance, HistoryBuilder.single(FeedKey()))
        backstackDelegate.registerForLifecycleCallbacks(activity)
        backstackDelegate.setStateChanger(stateChanger)
    }

    fun selectFragment(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_feed -> replaceHistory(FeedKey())
            R.id.action_trands -> replaceHistory(TrendsKey())
            R.id.action_playlists -> replaceHistory(PlaylistsKey())
            R.id.action_channels -> replaceHistory(ChannelsKey())
            R.id.action_profile -> replaceHistory(ProfileKey())
        }
        return true
    }


    fun replaceHistory(rootKey: Any) {
        backstackDelegate.backstack.setHistory(HistoryBuilder.single(rootKey), StateChange.REPLACE)
    }

    fun navigateTo(key: Any) {
        backstackDelegate.backstack.goTo(key)
    }

    var stateChanger = object : StateChanger {
        override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
            val topNewState: BaseKey = stateChange.topNewState()
            activityCallback(stateChange.topNewState())
            if (stateChange.topNewState<Any>() == stateChange.topPreviousState<Any>()) {
                completionCallback.stateChangeComplete()
                return
            }
            fragmentStateChanger.handleStateChange(stateChange, topNewState.menuType())
            completionCallback.stateChangeComplete()
        }


    }
}