package com.letitplay.maugry.letitplay.data_management.repo.trend

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.Listing
import io.reactivex.disposables.CompositeDisposable


interface TrendRepository {
    fun trends(compositeDisposable: CompositeDisposable): Listing<TrackWithChannel>
}