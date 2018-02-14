package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel


interface TrendRepository {
    fun trends(): LiveData<PagedList<TrackWithChannel>>
}