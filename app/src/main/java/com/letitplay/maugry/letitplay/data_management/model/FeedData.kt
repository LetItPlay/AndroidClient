package com.letitplay.maugry.letitplay.data_management.model

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel


data class FeedData(
        private val trackWithChannel: TrackWithChannel,
        val isLiked: Boolean = false
) {
    val track get() = trackWithChannel.track
    val channel get() = trackWithChannel.channel
}