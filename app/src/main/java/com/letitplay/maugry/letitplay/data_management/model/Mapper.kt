package com.letitplay.maugry.letitplay.data_management.model

import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.data_management.model.remote.responses.UpdatedChannelResponse
import com.letitplay.maugry.letitplay.data_management.model.remote.responses.UpdatedTrackResponse
import com.letitplay.maugry.letitplay.utils.ext.splitTags
import io.realm.RealmList


private fun <T> identity(x: T): T = x

fun toChannelModel(updatedChannelResponse: UpdatedChannelResponse): ChannelModel {
    return ChannelModel(
            id = updatedChannelResponse.id,
            lang = updatedChannelResponse.lang,
            name = updatedChannelResponse.name,
            imageUrl = updatedChannelResponse.imageUrl.fixServerPrefix(),
            subscriptionCount = updatedChannelResponse.subscriptionCount,
            tags = updatedChannelResponse.tags?.splitTags()?.mapTo(RealmList(), ::identity)
    )
}

fun toTrackModel(updatedTrackResponse: UpdatedTrackResponse): TrackModel {
    return TrackModel(
            id = updatedTrackResponse.id,
            lang = updatedTrackResponse.lang,
            stationId = updatedTrackResponse.stationId,
            title = updatedTrackResponse.name,
            description = updatedTrackResponse.description,
            coverUrl = updatedTrackResponse.imageUrl,
            audioUrl = updatedTrackResponse.audioFile.filePath.fixServerPrefix(),
            totalLengthInSeconds = updatedTrackResponse.audioFile.lengthInSeconds,
            likeCount = updatedTrackResponse.likeCount,
            tags = updatedTrackResponse.tags?.splitTags()?.mapTo(RealmList(), ::identity),
            listenCount = updatedTrackResponse.listenCount,
            publishedAt = updatedTrackResponse.publishedAt
    )
}

fun String.fixServerPrefix(): String =
        when {
            this.startsWith("http") -> this
            else -> GL_MEDIA_SERVICE_URL + this
        }