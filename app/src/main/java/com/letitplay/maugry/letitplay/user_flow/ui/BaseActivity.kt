package com.letitplay.maugry.letitplay.user_flow.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.profile.ProfileKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.FragmentStateChanger
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.HistoryBuilder
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import kotlinx.android.synthetic.main.navigation_main.*

abstract class BaseActivity(val layoutId: Int) : AppCompatActivity(), StateChanger {

    lateinit var backstackDelegate: BackstackDelegate
    lateinit var fragmentStateChanger: FragmentStateChanger
    var navigationMenu: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        backstackDelegate = BackstackDelegate(null)
        backstackDelegate.onCreate(savedInstanceState, lastCustomNonConfigurationInstance, HistoryBuilder.single(FeedKey))
        backstackDelegate.registerForLifecycleCallbacks(this)
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        navigationMenu = findViewById(R.id.navigation)
        setNavigationMenu()
        navigationMenu?.menu?.findItem(R.id.action_feed)?.isChecked = true
        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.fragment_container)
        backstackDelegate.setStateChanger(this)
    }

    private fun setNavigationMenu() {
        navigationMenu?.setOnNavigationItemSelectedListener { item: MenuItem -> selectFragment(item) }
    }

    private fun selectFragment(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_feed -> replaceHistory(FeedKey)
            R.id.action_channels -> replaceHistory(ChannelsKey)
            R.id.action_profile -> replaceHistory(ProfileKey)
        }
        return true
    }

    override fun onRetainCustomNonConfigurationInstance() =
            backstackDelegate.onRetainCustomNonConfigurationInstance()

    private fun replaceHistory(rootKey: Any) {
        backstackDelegate.backstack.setHistory(HistoryBuilder.single(rootKey), StateChange.REPLACE)
    }

    override fun onBackPressed() {
        if (!backstackDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }

    fun navigateTo(key: Any) {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setNavigationIcon(R.drawable.back)
        backstackDelegate.backstack.goTo(key)
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.topNewState<Any>() == stateChange.topPreviousState<Any>()) {
            completionCallback.stateChangeComplete()
        }
        fragmentStateChanger.handleStateChange(stateChange)
        completionCallback.stateChangeComplete()
    }
}