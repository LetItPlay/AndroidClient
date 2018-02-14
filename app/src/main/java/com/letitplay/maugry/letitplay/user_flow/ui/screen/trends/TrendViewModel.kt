package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.TrendRepository


class TrendViewModel(
        private val trendRepository: TrendRepository
) : ViewModel() {
    val trends: LiveData<PagedList<TrackWithChannel>> = trendRepository.trends()
}