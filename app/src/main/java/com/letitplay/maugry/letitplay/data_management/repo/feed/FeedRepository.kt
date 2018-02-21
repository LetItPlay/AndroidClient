package com.letitplay.maugry.letitplay.data_management.repo.feed

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.Listing


interface FeedRepository {
    fun feeds(): Listing<TrackWithChannel>
}