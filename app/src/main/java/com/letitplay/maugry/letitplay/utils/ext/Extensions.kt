package com.letitplay.maugry.letitplay.utils.ext

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel

fun String.splitTags(): List<String> =
    this.split(",")
            .map(String::trim)
            .filter(String::isNotEmpty)

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