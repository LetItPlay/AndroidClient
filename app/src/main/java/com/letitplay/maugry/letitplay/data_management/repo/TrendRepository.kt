package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.paging.PagedList
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Flowable


interface TrendRepository {
    fun trends(): Flowable<PagedList<TrackWithChannel>>
}