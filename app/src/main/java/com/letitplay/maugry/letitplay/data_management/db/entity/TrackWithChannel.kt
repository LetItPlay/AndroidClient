package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.Embedded

data class TrackWithChannel(
        @Embedded
        val track: Track,
        @Embedded
        val channel: Channel,
        val likeId: Int?
) {
    val isLike get() = likeId != null
}