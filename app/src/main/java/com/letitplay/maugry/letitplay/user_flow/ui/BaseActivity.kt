package com.letitplay.maugry.letitplay.user_flow.ui

import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.design.widget.BottomNavigationView
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.App
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.profile.ProfileKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.SearchResultsKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.trends.TrendsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.FragmentStateChanger
import com.letitplay.maugry.letitplay.user_flow.ui.widget.MusicPlayerSmall
import com.letitplay.maugry.letitplay.utils.active
import com.letitplay.maugry.letitplay.utils.disableShiftMode
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.HistoryBuilder
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import kotlinx.android.synthetic.main.navigation_main.*
import kotlinx.android.synthetic.main.player_container_fragment.view.*

abstract class BaseActivity(val layoutId: Int) : AppCompatActivity(), StateChanger {

    lateinit var backstackDelegate: BackstackDelegate
    lateinit var fragmentStateChanger: FragmentStateChanger
    var navigationMenu: BottomNavigationView? = null

    protected val musicService: MusicService?
        get() = (application as App).musicService

    val musicPlayerSmall: MusicPlayerSmall?
        get() {
            if (music_player_small.mediaSession == null) {
                music_player_small.mediaSession = musicService?.mediaSession

            }
            return music_player_small
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        backstackDelegate = BackstackDelegate(null)
        backstackDelegate.onCreate(savedInstanceState, lastCustomNonConfigurationInstance, HistoryBuilder.single(FeedKey()))
        backstackDelegate.registerForLifecycleCallbacks(this)
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        navigationMenu = findViewById(R.id.navigation)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        setNavigationMenu()
        navigationMenu?.disableShiftMode()
        navigationMenu?.active(R.id.action_feed)
        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.fragment_container)
        backstackDelegate.setStateChanger(this)
        setSupportActionBar(toolbar)
    }

    fun updateRepo(trackId: Long, repo: MusicRepo?) {
        musicService?.musicRepo = repo
        musicPlayerSmall?.apply {
            setOnClickListener { expandPlayer() }
            visibility = View.VISIBLE
            skipToQueueItem(trackId)
        }

    }


    private fun setNavigationMenu() {
        navigationMenu?.setOnNavigationItemSelectedListener { item: MenuItem -> selectFragment(item) }
    }

    private fun selectFragment(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_feed -> replaceHistory(FeedKey())
            R.id.action_trands -> replaceHistory(TrendsKey())
            R.id.action_channels -> replaceHistory(ChannelsKey())
            R.id.action_search -> replaceHistory(SearchResultsKey())
            R.id.action_profile -> replaceHistory(ProfileKey())
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

    private fun setBackNavigationIcon(key: BaseKey) {
        if (key.isRootFragment()) toolbar.navigationIcon = null
        else {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            toolbar.setNavigationIcon(R.drawable.back)
        }

        when (key.menuType()) {
            MenuType.PLAYER -> {
                musicPlayerSmall?.visibility = View.GONE
                navigationMenu?.visibility = View.GONE
            }

            else -> {
                navigationMenu?.visibility = View.VISIBLE
                musicPlayerSmall?.let {
                    if (it.isPlaying()) it.visibility = View.VISIBLE
                }
            }
        }
    }

    fun collapsePlayer() {
        navigationMenu?.visibility = View.VISIBLE
        appbar?.visibility = View.VISIBLE

        val set = ConstraintSet()
        set.connect(R.id.main_player, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(R.id.main_player, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(R.id.main_player, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        TransitionManager.beginDelayedTransition(root_constraint)
        set.applyTo(root_constraint)
        main_player.releaseViewPager()
    }

    fun expandPlayer() {
        main_player.setViewPager(supportFragmentManager)
        val set = ConstraintSet()
        set.connect(R.id.main_player, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        set.connect(R.id.main_player, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(R.id.main_player, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        set.connect(R.id.main_player, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        navigationMenu?.visibility = View.INVISIBLE
        appbar?.visibility = View.INVISIBLE
        TransitionManager.beginDelayedTransition(root_constraint)
        set.applyTo(root_constraint)
        main_player.collapse.setOnClickListener {
            collapsePlayer()
        }
    }

    fun navigateTo(key: Any) {
        backstackDelegate.backstack.goTo(key)
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        val topNewState: BaseKey = stateChange.topNewState()
        setBackNavigationIcon(stateChange.topNewState())
        if (stateChange.topNewState<Any>() == stateChange.topPreviousState<Any>()) {
            completionCallback.stateChangeComplete()
            return
        }
        fragmentStateChanger.handleStateChange(stateChange, topNewState.menuType())
        completionCallback.stateChangeComplete()
    }
}