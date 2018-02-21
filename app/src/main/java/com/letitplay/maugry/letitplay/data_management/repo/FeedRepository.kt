package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel


interface FeedRepository {
    fun feeds(): Listing<TrackWithChannel>
}