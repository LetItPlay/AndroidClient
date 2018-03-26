package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.ui.MusicPlayer
import com.letitplay.maugry.letitplay.R
import kotlinx.android.synthetic.main.track_detail_widget.view.*


class TrackDetailedWidget @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : MusicPlayer(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.track_detail_widget, this)
    }

    override fun updateButtonsStates() {

    }

    override fun updateTrackInfo(metadata: MediaMetadataCompat) {
        track_detailed_channel_title.text = metadata.description.subtitle
        track_detailed_track_title.text = metadata.description?.title
        Glide.with(context)
                .load(metadata.description.iconUri)
                .into(track_detailed_channel_logo)


    }

    override fun updateDuration(durationMs: Long) {

    }

    override fun updateCurrentPosition(positionMs: Long) {

    }
}