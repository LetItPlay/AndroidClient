package com.letitplay.maugry.letitplay.data_management.model

import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel


private fun <T> identity(x: T): T = x

fun toTrackModel(trackEntity: Track, channelEntity: Channel): TrackWithChannel {
    return TrackWithChannel(
            trackEntity,
            channelEntity
    )
}

//fun toChannelModel(updatedChannelResponse: UpdatedChannelResponse): Channel {
//    return Channel(
//            id = updatedChannelResponse.id,
//            lang = updatedChannelResponse.lang,
//            name = updatedChannelResponse.name,
//            imageUrl = updatedChannelResponse.imageUrl.fixServerPrefix(),
//            subscriptionCount = updatedChannelResponse.subscriptionCount,
//            tags = updatedChannelResponse.tags?.splitTags()?.mapTo(RealmList(), ::identity)
//    )
//}
//
//fun toTrackModel(updatedTrackResponse: UpdatedTrackResponse): TrackWithChannel {
//    return TrackWithChannel(
//            id = updatedTrackResponse.id,
//            lang = updatedTrackResponse.lang,
//            stationId = updatedTrackResponse.stationId,
//            title = updatedTrackResponse.name,
//            description = updatedTrackResponse.description,
//            coverUrl = updatedTrackResponse.imageUrl,
//            audioUrl = updatedTrackResponse.audioFile.filePath.fixServerPrefix(),
//            totalLengthInSeconds = updatedTrackResponse.audioFile.lengthInSeconds,
//            likeCount = updatedTrackResponse.likeCount,
//            tags = updatedTrackResponse.tags?.splitTags()?.mapTo(RealmList(), ::identity),
//            listenCount = updatedTrackResponse.listenCount,
//            publishedAt = updatedTrackResponse.publishedAt
//    )
//}