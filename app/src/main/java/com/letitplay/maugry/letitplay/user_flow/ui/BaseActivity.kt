package com.letitplay.maugry.letitplay.user_flow.ui

import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.App
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.global.PlayerViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists.PlaylistsKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.profile.ProfileKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.trends.TrendsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.FragmentStateChanger
import com.letitplay.maugry.letitplay.user_flow.ui.utils.SimpleBottomSheetCallback
import com.letitplay.maugry.letitplay.user_flow.ui.widget.MusicPlayerSmall
import com.letitplay.maugry.letitplay.utils.ext.active
import com.letitplay.maugry.letitplay.utils.ext.disableShiftMode
import com.letitplay.maugry.letitplay.utils.ext.show
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.HistoryBuilder
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import kotlinx.android.synthetic.main.navigation_main.*

abstract class BaseActivity(val layoutId: Int) : AppCompatActivity(), StateChanger {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var backstackDelegate: BackstackDelegate
    private lateinit var fragmentStateChanger: FragmentStateChanger

    private var navigationMenu: BottomNavigationView? = null
    private val playerViewModel by lazy {
        ViewModelProvider(viewModelStore, ServiceLocator.viewModelFactory).get(PlayerViewModel::class.java)
    }

    protected val musicService: MusicService?
        get() = (application as App).musicService

    val musicPlayerSmall: MusicPlayerSmall?
        get() {
            if (music_player_small.mediaSession == null) {
                music_player_small.mediaSession = musicService?.mediaSession
            }
            return music_player_small
        }
    val toolbar get() = (main_toolbar as? Toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        backstackDelegate = BackstackDelegate(null)
        ServiceLocator.backstackDelegate = backstackDelegate
        backstackDelegate.onCreate(savedInstanceState, lastCustomNonConfigurationInstance, HistoryBuilder.single(FeedKey()))
        backstackDelegate.registerForLifecycleCallbacks(this)
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        navigationMenu = findViewById(R.id.navigation)
        toolbar?.setNavigationOnClickListener { onBackPressed() }
        setNavigationMenu()
        navigationMenu?.disableShiftMode()
        navigationMenu?.active(R.id.action_feed)
        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.fragment_container)
        backstackDelegate.setStateChanger(this)
        setSupportActionBar(toolbar)
        bottomSheetBehavior = BottomSheetBehavior.from(main_player)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setBottomSheetCallback(object : SimpleBottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    collapsePlayer()
                }
            }
        })
        main_player.apply {
            this@BaseActivity.playerViewModel.apply {
                setMusicService(musicService)
            }
            setup(::collapsePlayer, this@BaseActivity.playerViewModel)
        }
    }

    fun updateRepo(trackId: Int, repo: MusicRepo?, tracks: List<TrackWithChannel>) {
        musicService?.musicRepo = repo
        playerViewModel.setMusicRepo(repo, tracks)
        musicPlayerSmall?.apply {
            setOnClickListener {
                expandPlayer()
            }
            show()
            skipToQueueItem(trackId)
        }

    }

    fun addTrackToStartRepo(track: AudioTrack) {
        musicService?.addTrackToStart(track)
    }

    fun addTrackToEndRepo(track: AudioTrack) {
        musicService?.addTrackToEnd(track)
    }

    fun removeTrack(id: Int) {
        musicService?.removeTrack(id)
    }


    private fun setNavigationMenu() {
        navigationMenu?.setOnNavigationItemSelectedListener { item: MenuItem -> selectFragment(item) }
    }

    private fun selectFragment(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_feed -> replaceHistory(FeedKey())
            R.id.action_trands -> replaceHistory(TrendsKey())
            R.id.action_playlists -> replaceHistory(PlaylistsKey())
            R.id.action_channels -> replaceHistory(ChannelsKey())
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
        if (key.isRootFragment()) toolbar?.navigationIcon = null
        else {
            toolbar?.setNavigationOnClickListener { onBackPressed() }
            toolbar?.setNavigationIcon(R.drawable.back)
        }

        when (key.menuType()) {
            MenuType.PLAYER -> {
                musicPlayerSmall?.visibility = View.GONE
                navigationMenu?.visibility = View.GONE
            }

            MenuType.PLAYLISTS -> {
                toolbar?.visibility = View.GONE
            }
            else -> {
                toolbar?.visibility = View.VISIBLE
                navigationMenu?.visibility = View.VISIBLE
                musicPlayerSmall?.let {
                    if (it.isPlaying()) it.visibility = View.VISIBLE
                }
            }
        }
    }

    fun collapsePlayer() {
        main_player.setCollapsedState()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun expandPlayer() {
        main_player.setExpandedState()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun navigateTo(key: Any) {
        backstackDelegate.backstack.goTo(key)
    }

    fun replaceHistory(menuItem: Int) {
        selectFragment(navigationMenu?.menu?.findItem(menuItem))
        navigationMenu?.active(menuItem)
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