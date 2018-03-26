package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.FragmentManager
import android.util.AttributeSet
import android.view.LayoutInflater
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.model.PlaybackSpeed
import com.letitplay.maugry.letitplay.data_management.model.availableSpeeds
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.PlayerContainerAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.PlayerViewModel
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import kotlinx.android.synthetic.main.player_container_fragment.view.*
import kotlinx.android.synthetic.main.player_fragment.view.*
import kotlinx.android.synthetic.main.track_detail_fragment.view.*


class PlayerWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    var isExpanded: Boolean = false
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

            player_like_button.setOnClickListener {

            }
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
        player_pager.adapter = PlayerContainerAdapter(fm)
    }

    fun onExpand(musicService: MusicService?) {
        isExpanded = true
        music_player_big.apply {
            mediaSession = musicService?.mediaSession
        }
    }

    fun onCollapse() {
        isExpanded = false
        music_player_big.mediaSession = null
    }

}