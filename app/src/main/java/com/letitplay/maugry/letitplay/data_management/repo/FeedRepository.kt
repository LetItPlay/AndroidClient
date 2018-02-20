package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.paging.PagedList
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.Result
import io.reactivex.Flowable


interface FeedRepository {
    fun feeds(): Flowable<Result<PagedList<TrackWithChannel>>>
}