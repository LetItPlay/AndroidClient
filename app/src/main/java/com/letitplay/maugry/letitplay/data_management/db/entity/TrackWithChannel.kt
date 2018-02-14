package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore

data class TrackWithChannel(
        @Embedded
        val track: Track,
        @Embedded
        val channel: Channel,
        val likeId: Int?
) {
    val isLiked get() = likeId != null
    @Ignore
    var index: Int = -1
}