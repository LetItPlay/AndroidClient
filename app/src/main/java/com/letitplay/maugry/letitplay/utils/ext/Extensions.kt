package com.letitplay.maugry.letitplay.utils.ext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gsfoxpro.musicservice.model.AudioTrack
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

fun ImageView.loadImage(url: String?, context: Context? = null) {
    if (url != null) {
        Glide.with(context ?: this.context)
                .load(url)
                .into(this)
    }
}

fun ImageView.loadCircularImage(url: String?, context: Context? = null) {
    if (url != null) {
        Glide.with(context ?: this.context)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(this)
    }
}

fun String.splitTags(): List<String> =
    this.split(",")
            .map(String::trim)
            .filter(String::isNotEmpty)

fun TextView.updateText(text: CharSequence?) {
    if (this.text != text) {
        this.text = text
    }
}

fun Pair<ChannelModel?, TrackModel?>.toAudioTrack(): AudioTrack {
    val (channel, track) = this

    return AudioTrack(
            id = track?.id!!,
            url = track.audioUrl ?: "",
            title = track.title,
            subtitle = channel?.name,
            imageUrl = track.coverUrl ?: "",
            channelTitle = channel?.name,
            length = track.totalLengthInSeconds,
            listenCount = track.listenCount,
            publishedAt = track.publishedAt
    )
}