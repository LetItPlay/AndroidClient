package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.ui.MusicPlayer
import com.letitplay.maugry.letitplay.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.music_player_small.view.*

class MusicPlayerSmall : MusicPlayer {

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    var subject: PublishSubject<String>? = null

    init {
        subject = PublishSubject.create()
        LayoutInflater.from(context).inflate(R.layout.music_player_small, this)

        play_pause_button.setOnClickListener { playPause() }
        next_button.setOnClickListener { next() }
    }

    override fun updateButtonsStates() {
        when (playing) {
            true -> play_pause_button.setImageResource(R.drawable.ic_pause_black_24dp)
            else -> play_pause_button.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
        next_button?.alpha = if (hasNext) 1F else 0.5F
    }

    override fun updateTrackInfo(metadata: MediaMetadataCompat) {
        title.text = metadata.description?.title
        subtitle.text = metadata.description?.subtitle
        Glide.with(context)
                .load(metadata.description.iconUri)
                .into(cover)
        subject?.doOnNext {
            metadata.description.mediaUri.toString()
        }
    }

    fun isPlaying() = playing || pausing

    override fun updateDuration(durationMs: Long) {
        progress.max = durationMs.toInt()
    }

    override fun updateCurrentPosition(positionMs: Long) {
        progress.progress = positionMs.toInt()
    }
}