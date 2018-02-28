package com.letitplay.maugry.letitplay.data_management.repo.feed

import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.Listing
import io.reactivex.Flowable


interface FeedRepository {
    fun feeds(): Listing<TrackWithChannel>
    fun likes(): Flowable<List<Like>>
}