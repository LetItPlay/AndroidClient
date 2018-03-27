package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.gsfoxpro.musicservice.ui.MusicPlayer
import com.letitplay.maugry.letitplay.R
import kotlinx.android.synthetic.main.playing_now_widget.view.*


class PlayingNowWidget @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : MusicPlayer(context, attrs, defStyleAttr) {

    var trackId: String? = null
    var trackListenerCount: Int? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.playing_now_widget, this)
    }

    override fun updateButtonsStates() {

    }

    override fun updateTrackInfo(metadata: MediaMetadataCompat) {
        if (metadata.description.mediaId == trackId) {
            showNowPlaying()
        }
        else {
            showListenCount()
        }
    }

    private fun showNowPlaying() {
        feed_playing_now_icon.visibility = View.VISIBLE
        feed_playing.visibility = View.GONE
        playing_now.visibility = View.VISIBLE
        listener_count.visibility = View.GONE
    }

    private fun showListenCount() {
        feed_playing_now_icon.visibility = View.GONE
        feed_playing.visibility = View.VISIBLE
        listener_count.visibility = View.VISIBLE
        playing_now.visibility = View.GONE
        listener_count.text = trackListenerCount.toString()
    }

    override fun updateDuration(durationMs: Long) {

    }

    override fun updateCurrentPosition(positionMs: Long) {

    }

}