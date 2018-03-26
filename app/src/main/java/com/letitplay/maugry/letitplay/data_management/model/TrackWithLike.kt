package com.letitplay.maugry.letitplay.data_management.model

import android.arch.persistence.room.Embedded
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.Track


data class TrackWithLike(
        @Embedded
        val track: Track,
        @Embedded
        val like: Like?
)