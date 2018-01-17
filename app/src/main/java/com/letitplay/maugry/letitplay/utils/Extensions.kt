package com.letitplay.maugry.letitplay.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel

/*
*       GROUP INFLATION
* */

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)

fun ViewGroup.inflateHolder(layoutId: Int): View =
        LayoutInflater.from(context).inflate(layoutId, this, false)

@Suppress("UNCHECKED_CAST")
fun <T> inflate(layoutId: Int, context: Context) = LayoutInflater.from(context).inflate(layoutId, null) as T

fun ImageView.loadImage(context: Context, url: String?, prefix: String = GL_MEDIA_SERVICE_URL) {
    if (url != null) {
        Glide.with(context)
                .load("$prefix$url")
                .into(this)
    }
}

fun TextView.updateText(text: CharSequence?) {
    if (this.text != text) {
        this.text = text
    }
}

fun Pair<ChannelModel, TrackModel>.toAudioTrack(): AudioTrack {
    val (channel, track) = this
    return AudioTrack(
            id = track.id!!,
            url = "$GL_MEDIA_SERVICE_URL${track.audio?.fileUrl}",
            title = track.name,
            subtitle = channel.name,
            imageUrl = "$GL_MEDIA_SERVICE_URL${track.image}",
            channelTitle = channel.name,
            length = track.audio?.lengthInSeconds,
            listenCount = track.listenCount,
            publishedAt = track.publishedAt
    )
}