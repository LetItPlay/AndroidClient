package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.profile.ProfileRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.Optional
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.rxkotlin.addTo


class ProfileViewModel(
        private val profileRepository: ProfileRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel(), LifecycleObserver {

    val language: LiveData<Optional<Language>> by lazy {
        profileRepository.getLanguage().toLiveData()
    }

    val likedTracks: LiveData<List<TrackWithChannel>> by lazy {
        Transformations.switchMap(language, {
            profileRepository.likedTracks()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .toLiveData()
        })
    }

    fun flipLanguage() {
        profileRepository.flipLanguage()
                .subscribe()
                .addTo(compositeDisposable)
    }
}