package com.letitplay.maugry.letitplay.data_management.model

import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track


private fun <T> identity(x: T): T = x

fun toTrackModel(trackEntity: Track, channelEntity: Channel): com.letitplay.maugry.letitplay.data_management.model.Track {
    return com.letitplay.maugry.letitplay.data_management.model.Track(
            trackEntity.id,
            trackEntity.lang,
            trackEntity.title,
            trackEntity.description,
            trackEntity.coverUrl,
            trackEntity.audioUrl,
            trackEntity.totalLengthInSeconds,
            trackEntity.likeCount,
            trackEntity.tags,
            trackEntity.listenCount,
            trackEntity.publishedAt,
            toChannelModel(channelEntity)
    )
}

fun toChannelModel(channelEntity: Channel): com.letitplay.maugry.letitplay.data_management.model.Channel {
    return com.letitplay.maugry.letitplay.data_management.model.Channel(
            channelEntity.id,
            channelEntity.lang,
            channelEntity.name,
            channelEntity.imageUrl,
            channelEntity.subscriptionCount,
            channelEntity.tags
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
//fun toTrackModel(updatedTrackResponse: UpdatedTrackResponse): Track {
//    return Track(
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