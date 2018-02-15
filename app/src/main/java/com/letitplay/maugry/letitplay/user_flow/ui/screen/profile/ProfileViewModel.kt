package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.arch.lifecycle.ViewModel
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.repo.ProfileRepository
import com.letitplay.maugry.letitplay.utils.ext.toLiveData


class ProfileViewModel(
        private val profileRepository: ProfileRepository,
        private val schedulerProvider: SchedulerProvider
) : ViewModel() {
    val likedTracks by lazy {
        profileRepository.likedTracks()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .toLiveData()
    }
}