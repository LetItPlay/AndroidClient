package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.AttributeSet
import android.view.LayoutInflater
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.PlaybackSpeed
import com.letitplay.maugry.letitplay.data_management.model.availableSpeeds
import com.letitplay.maugry.letitplay.user_flow.ui.screen.global.PlayerViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.PlayerFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.PlaylistFragment
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import kotlinx.android.synthetic.main.player_container_fragment.view.*
import kotlinx.android.synthetic.main.player_fragment.view.*

class PlayerWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    var isExpanded: Boolean = false
    var onCollapseClick: () -> Unit = {}
    var playerViewModel: PlayerViewModel? = null
    private val playerFragment by lazy { PlayerFragment() }
    private val playlistFragment by lazy { PlaylistFragment() }
    private val preferenceHelper = PreferenceHelper(context)

    init {
        LayoutInflater.from(context).inflate(R.layout.player_container_fragment, this)
        if (!isInEditMode) {
            player_playback_button.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(context)
                val availSpeedList = availableSpeeds()
                val currentSpeedIndex = availSpeedList.indexOf(preferenceHelper.playbackSpeed)
                val optionsDialog = PlaybackSpeedDialog(context).apply {
                    setItems(availSpeedList, currentSpeedIndex)
                    onOptionClick = { onPlaybackSpeedOptionClick(this, availSpeedList, it) }
                }
                bottomSheetDialog.setContentView(optionsDialog)
                bottomSheetDialog.show()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        playerViewModel?.currentTrackIsLiked?.observeForever {
            if (it != null) {
                setLikeState(it)
            }
        }
        player_like_button.setOnClickListener {
            playerViewModel?.likeCurrentTrack()
        }
        collapse.setOnClickListener {
            onCollapseClick()
        }
    }

    private fun onPlaybackSpeedOptionClick(dialog: PlaybackSpeedDialog, options: List<PlaybackSpeed>, playbackSpeed: PlaybackSpeed) {
        val player = music_player_big
        player.changePlaybackSpeed(playbackSpeed.value)
        dialog.selectAt(options.indexOf(playbackSpeed))
        preferenceHelper.playbackSpeed = playbackSpeed
    }

    fun setViewPager(fm: FragmentManager) {
        player_tabs.setupWithViewPager(player_pager)
        player_pager.adapter = PlayerTabsAdapter(fm)
    }

    fun setExpandedState(musicService: MusicService?) {
        isExpanded = true
        music_player_big.apply {
            mediaSession = musicService?.mediaSession
        }
    }

    fun setCollapsedState() {
        isExpanded = false
        music_player_big.mediaSession = null
    }

    private fun setLikeState(isLiked: Boolean) {
        player_like_button.setImageResource(if (isLiked) R.drawable.ic_like else R.drawable.ic_dislike)
    }

    inner class PlayerTabsAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> playerFragment
                1 -> playlistFragment
                else -> throw IllegalArgumentException()
            }
        }

        override fun getCount(): Int = 2
    }
}