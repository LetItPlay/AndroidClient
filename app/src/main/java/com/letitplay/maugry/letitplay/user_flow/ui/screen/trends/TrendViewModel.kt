package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.lifecycle.ViewModel
import com.letitplay.maugry.letitplay.data_management.model.Track
import com.letitplay.maugry.letitplay.data_management.repo.TrackRepository
import io.reactivex.Single


class TrendViewModel(
        private val trackRepository: TrackRepository
) : ViewModel() {
    val trends: Single<List<Track>> = trackRepository.trends()

}