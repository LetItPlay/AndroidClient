package com.letitplay.maugry.letitplay.data_management.repo.feed

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.Listing
import io.reactivex.disposables.CompositeDisposable


interface FeedRepository {
    fun feeds(compositeDisposable: CompositeDisposable): Listing<TrackWithChannel>
}