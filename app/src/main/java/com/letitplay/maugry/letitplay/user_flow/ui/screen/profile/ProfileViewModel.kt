package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.profile.ProfileRepository
import com.letitplay.maugry.letitplay.data_management.repo.track.TrackRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.Optional
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber


class ProfileViewModel(
        private val trackRepository: TrackRepository,
        private val profileRepository: ProfileRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    private var reportDisposable: Disposable? = null

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

    fun changeLanguage(shortStr: String) {
        val lang = Language.fromString(shortStr) ?: throw IllegalArgumentException()
        profileRepository.changeLanguage(lang)
                .subscribe()
                .addTo(compositeDisposable)
    }

    fun onReportClick(trackId:Int, reason: Int) {
        if (reportDisposable == null || reportDisposable!!.isDisposed) {
            reportDisposable = trackRepository.report(trackId,reason)
                    .subscribe({}, {
                        Timber.e(it, "Error when liking")
                    })
        }

    }

}