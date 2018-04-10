package com.letitplay.maugry.letitplay.user_flow.ui

import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import biz.laenger.android.vpbs.BottomSheetUtils
import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.App
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.global.PlayerViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.widget.MusicPlayerSmall
import com.letitplay.maugry.letitplay.utils.ext.active
import com.letitplay.maugry.letitplay.utils.ext.disableShiftMode
import com.zhuinden.simplestack.StateChanger
import kotlinx.android.synthetic.main.navigation_main.*
import kotlinx.android.synthetic.main.player_container_fragment.*

abstract class BaseActivity(private val layoutId: Int) : AppCompatActivity(){

    private lateinit var bottomSheetBehavior: ViewPagerBottomSheetBehavior<View>
    protected var navigationMenu: BottomNavigationView? = null
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
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        setSupportActionBar(toolbar)
        toolbar?.setNavigationOnClickListener { onBackPressed() }
        initNavigationMenu()
        initPlayer()
        Navigator.setupBackstackDelegate(savedInstanceState, lastCustomNonConfigurationInstance,
                supportFragmentManager, this, ::setBackNavigationIcon)
    }

    private fun initNavigationMenu() {
        navigationMenu = findViewById<BottomNavigationView>(R.id.navigation).apply {
            setOnNavigationItemSelectedListener { item: MenuItem -> Navigator.selectFragment(item) }
            disableShiftMode()
            active(R.id.action_feed)
        }
    }

    private fun initPlayer() {
        bottomSheetBehavior = ViewPagerBottomSheetBehavior.from(main_player)
        bottomSheetBehavior.apply {
            state = ViewPagerBottomSheetBehavior.STATE_COLLAPSED
            setBottomSheetCallback(
                    object : ViewPagerBottomSheetBehavior.BottomSheetCallback() {
                        override fun onSlide(bottomSheet: View, slideOffset: Float) {

                        }

                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            if (newState == ViewPagerBottomSheetBehavior.STATE_COLLAPSED) {
                                collapsePlayer()
                            }
                        }
                    }
            )
        }
        main_player.apply {
            this@BaseActivity.playerViewModel.apply {
                setMusicService(musicService)
            }
            setup(::collapsePlayer, this@BaseActivity.playerViewModel)
        }
        musicPlayerSmall?.apply {
            setOnClickListener {
                expandPlayer()
            }
            setup(this@BaseActivity.playerViewModel)
        }
        BottomSheetUtils.setupViewPager(player_pager)
    }

    fun updateRepo(trackId: Int, repo: MusicRepo?, tracks: List<TrackWithChannel>) {
        playerViewModel.setMusicRepo(repo, tracks)
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


    override fun onRetainCustomNonConfigurationInstance() = Navigator.backstackDelegate.onRetainCustomNonConfigurationInstance()


    override fun onBackPressed() {
        if (main_player.isExpanded) {
            collapsePlayer()
        } else {
            if (!Navigator.backstackDelegate.onBackPressed()) {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                this.startActivity(intent)
            }
        }
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
                playlists_tabs?.visibility = View.VISIBLE
            }
            else -> {
                playlists_tabs?.visibility = View.GONE
                navigationMenu?.visibility = View.VISIBLE
                musicPlayerSmall?.let {
                    if (it.isPlaying()) it.visibility = View.VISIBLE
                }
            }
        }
    }

    fun collapsePlayer() {
        main_player.setCollapsedState()
        bottomSheetBehavior.state = ViewPagerBottomSheetBehavior.STATE_COLLAPSED
    }

    fun expandPlayer() {
        main_player.setExpandedState()
        bottomSheetBehavior.state = ViewPagerBottomSheetBehavior.STATE_EXPANDED
    }
}