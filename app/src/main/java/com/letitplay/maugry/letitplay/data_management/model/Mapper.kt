package com.letitplay.maugry.letitplay.data_management.model

import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.data_management.api.responses.UpdatedChannelResponse
import com.letitplay.maugry.letitplay.data_management.api.responses.UpdatedTrackResponse
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.utils.ext.splitTags

fun toChannelModel(updatedChannelResponse: UpdatedChannelResponse): Channel {
    return Channel(
            id = updatedChannelResponse.id,
            lang = updatedChannelResponse.lang,
            name = updatedChannelResponse.name,
            imageUrl = updatedChannelResponse.imageUrl.fixMediaPrefix(),
            subscriptionCount = updatedChannelResponse.subscriptionCount,
            tags = updatedChannelResponse.tags?.splitTags()
    )
}

fun toTrackModel(updatedTrackResponse: UpdatedTrackResponse): Track {
    return Track(
            id = updatedTrackResponse.id,
            lang = updatedTrackResponse.lang,
            stationId = updatedTrackResponse.stationId,
            title = updatedTrackResponse.name,
            description = updatedTrackResponse.description,
            coverUrl = updatedTrackResponse.imageUrl?.fixMediaPrefix(),
            audioUrl = updatedTrackResponse.audioFile.filePath.fixMediaPrefix(),
            totalLengthInSeconds = updatedTrackResponse.audioFile.lengthInSeconds,
            likeCount = updatedTrackResponse.likeCount,
            tags = updatedTrackResponse.tags?.splitTags(),
            listenCount = updatedTrackResponse.listenCount,
            publishedAt = updatedTrackResponse.publishedAt
    )
}

fun String.fixMediaPrefix(): String =
        when {
            this.startsWith("http") -> this
            else -> GL_MEDIA_SERVICE_URL + this
        }