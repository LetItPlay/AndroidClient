package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.Embedded


data class ChannelWithFollow(
        @Embedded
        val channel: Channel,
        val followId: Int?
) {
        val isFollowing get() = followId != null
}
