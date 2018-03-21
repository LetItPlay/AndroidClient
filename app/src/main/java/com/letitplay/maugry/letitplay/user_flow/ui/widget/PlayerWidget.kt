package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.FragmentManager
import android.util.AttributeSet
import android.view.LayoutInflater
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.availableSpeeds
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.PlayerContainerAdapter
import kotlinx.android.synthetic.main.player_container_fragment.view.*
import kotlinx.android.synthetic.main.player_fragment.view.*


class PlayerWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {


    init {
        LayoutInflater.from(context).inflate(R.layout.player_container_fragment, this)
        if (!isInEditMode) {
            player_playback_button.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(context)
                val availSpeedList = availableSpeeds()
                val currentSpeedIndex = 0 // TODO:
                val optionsDialog = PlaybackSpeedDialog(context).apply {
                    setItems(availSpeedList, currentSpeedIndex)
                    onOptionClick = {
                        // Send command to service
                        // Change UI
                        this.selectAt(availSpeedList.indexOf(it))
                    }
                }
                bottomSheetDialog.setContentView(optionsDialog)
                bottomSheetDialog.show()
            }
        }
    }


    var isExpanded: Boolean = false
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