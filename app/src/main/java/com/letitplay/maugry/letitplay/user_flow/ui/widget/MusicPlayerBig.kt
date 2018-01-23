package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.ui.MusicPlayer
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.utils.updateText
import kotlinx.android.synthetic.main.music_player_big.view.*


class MusicPlayerBig : MusicPlayer {

    private var trackDurationMs: Long = 0

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.music_player_big, this)
        player_track_title.isSelected = true
        player_channel_title.isSelected = true
        player_pause_play.setOnClickListener { playPause() }
        player_next.setOnClickListener { next() }
        player_back.setOnClickListener { prev() }
        player_remote_next.setOnClickListener { seekTo(10*1000) }
        player_remote_back.setOnClickListener { seekTo(-10*1000) }
        initSeekBar(player_big_progress)
    }

    override fun updateButtonsStates() {
        when (playing) {
            true -> player_pause_play.setImageResource(R.drawable.pause_player_ic)
            else -> player_pause_play.setImageResource(R.drawable.play_player_ic)
        }
        player_next?.alpha = if (hasNext) 1F else 0.5F
        player_back?.alpha = if (hasPrev) 1F else 0.5F
    }

    override fun updateTrackInfo(metadata: MediaMetadataCompat) {
        player_channel_title.updateText(metadata.description?.title)
        player_track_title.updateText(metadata.description?.subtitle)
        Glide.with(context)
                .load(metadata.description.iconUri)
                .into(player_track_image)
    }

    override fun updateDuration(durationMs: Long) {
        trackDurationMs = durationMs
    }

    override fun updateCurrentPosition(positionMs: Long) {
        player_current_time.text = getUserFriendlyTime(positionMs)
        player_time_left.text = "-${getUserFriendlyTime(trackDurationMs - positionMs)}"
    }
}