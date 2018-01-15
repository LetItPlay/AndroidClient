package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import com.gsfoxpro.musicservice.ui.MusicPlayer
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import kotlinx.android.synthetic.main.playing_now_widget.view.*


class PlayingNowWidget : MusicPlayer {

    var trackModel: TrackModel? = null

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.playing_now_widget, this)
    }

    override fun updateButtonsStates() {

    }


    override fun updateTrackInfo(metadata: MediaMetadataCompat) {
        if (metadata.description.mediaUri.toString() == "${GL_MEDIA_SERVICE_URL}${trackModel?.audio?.fileUrl}")
            listener_count.text = context.getString(R.string.playing_now)
        else listener_count.text = trackModel?.listenCount.toString()
    }

    override fun updateDuration(durationMs: Long) {

    }

    override fun updateCurrentPosition(positionMs: Long) {

    }

}