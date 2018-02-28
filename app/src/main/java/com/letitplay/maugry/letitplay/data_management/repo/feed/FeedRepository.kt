package com.letitplay.maugry.letitplay.data_management.repo.feed

import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.Listing
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable


interface FeedRepository {
    fun feeds(compositeDisposable: CompositeDisposable): Listing<TrackWithChannel>
    fun likes(): Flowable<List<Like>>
}