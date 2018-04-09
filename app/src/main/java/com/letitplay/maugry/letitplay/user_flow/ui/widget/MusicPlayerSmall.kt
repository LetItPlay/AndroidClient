package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.ui.MusicPlayer
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.screen.global.PlayerViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.CurrentPlaylistAdapter
import com.letitplay.maugry.letitplay.utils.ext.show
import com.letitplay.maugry.letitplay.utils.ext.updateText
import kotlinx.android.synthetic.main.music_player_small.view.*

class MusicPlayerSmall @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : MusicPlayer(context, attrs, defStyleAttr) {

    lateinit var playerViewModel: PlayerViewModel
    init {
        LayoutInflater.from(context).inflate(R.layout.music_player_small, this)

        play_pause_button.setOnClickListener { playPause() }
        next_button.setOnClickListener { next() }
        title.isSelected = true
        subtitle.isSelected = true
    }

    override fun updateButtonsStates() {
        when (playing) {
            true -> play_pause_button.setImageResource(R.drawable.ic_pause_black_24dp)
            else -> play_pause_button.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
        next_button?.alpha = if (hasNext) 1F else 0.5F
    }

    override fun updateTrackInfo(metadata: MediaMetadataCompat) {
        title.updateText(metadata.description?.title)
        subtitle.updateText(metadata.description?.subtitle)
        Glide.with(context)
                .load(metadata.description.iconUri)
                .into(cover)
    }

    fun isPlaying() = playing || pausing

    override fun updateDuration(durationMs: Long) {
        progress.max = durationMs.toInt()
    }

    override fun updateCurrentPosition(positionMs: Long) {
        progress.progress = positionMs.toInt()
    }

    fun onMusicRepoUpdate(repo: MusicRepo){
        show()
    }

    fun setup (vm: PlayerViewModel){
        this.playerViewModel = vm
        playerViewModel.musicRepo.observeForever {
            if (it != null) onMusicRepoUpdate(it)
        }
    }
}