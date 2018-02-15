package com.letitplay.maugry.letitplay.user_flow.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.App
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.profile.ProfileKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.PlaylistKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.trends.TrendsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.FragmentStateChanger
import com.letitplay.maugry.letitplay.user_flow.ui.widget.MusicPlayerSmall
import com.letitplay.maugry.letitplay.utils.ext.active
import com.letitplay.maugry.letitplay.utils.ext.disableShiftMode
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.HistoryBuilder
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import kotlinx.android.synthetic.main.navigation_main.*
import kotlinx.android.synthetic.main.player_container_fragment.view.*

abstract class BaseActivity(val layoutId: Int) : AppCompatActivity(), StateChanger {

    lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
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
        ServiceLocator.backstackDelegate = backstackDelegate
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
        mBottomSheetBehavior = BottomSheetBehavior.from(main_player)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        mBottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    collapsePlayer()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        main_player.apply {
            collapse.setOnClickListener {
                collapsePlayer()
            }
        }
        main_player.setViewPager(supportFragmentManager)
    }

    fun updateRepo(trackId: Int, repo: MusicRepo?) {
        musicService?.musicRepo = repo
        musicPlayerSmall?.apply {
            setOnClickListener {
                expandPlayer()
            }
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
            R.id.action_search -> replaceHistory(PlaylistKey())
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
        if (main_player.isExpanded) {
            collapsePlayer()
        } else {
            if (!backstackDelegate.onBackPressed()) {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                this.startActivity(intent)
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
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
        main_player.onCollapse()
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun expandPlayer() {
        main_player.onExpand(musicService)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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