package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Completable
import io.reactivex.Flowable


interface TrendRepository {
    fun trends(): Flowable<List<TrackWithChannel>>
    fun loadTrends(): Completable
}