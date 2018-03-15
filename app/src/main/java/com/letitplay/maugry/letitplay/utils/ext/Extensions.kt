package com.letitplay.maugry.letitplay.utils.ext

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel

fun String.splitTags(): List<String> =
        this.split(",")
                .map(String::trim)
                .filter(String::isNotEmpty)

fun List<Int>.joinWithComma() = this.joinToString(",")

fun TrackWithChannel.toAudioTrack(): AudioTrack {
    return AudioTrack(
            id = track.id,
            url = track.audioUrl ?: "",
            title = track.title,
            subtitle = channel.name,
            imageUrl = track.coverUrl ?: "",
            channelTitle = channel.name,
            lengthInMs = (track.totalLengthInSeconds * 1000).toLong(),
            listenCount = track.listenCount,
            publishedAt = track.publishedAt
    )
}