package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.profile.ProfileRepository
import com.letitplay.maugry.letitplay.data_management.repo.track.TrackRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.ext.toLiveData


class PlayerViewModel(
        private val profileRepository: ProfileRepository,
        private val trackRepository: TrackRepository,
        private val schedulerProvider: SchedulerProvider)
    : BaseViewModel(), LifecycleObserver {


    val likedTrack: LiveData<List<TrackWithChannel>>  by lazy {
        profileRepository.likedTracks()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .toLiveData()
    }

}