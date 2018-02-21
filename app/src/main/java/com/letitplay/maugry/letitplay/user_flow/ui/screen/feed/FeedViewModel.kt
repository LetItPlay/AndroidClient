package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.FeedRepository
import com.letitplay.maugry.letitplay.data_management.repo.PlayerRepository
import com.letitplay.maugry.letitplay.data_management.repo.TrackRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo


class FeedViewModel(
        private val feedRepository: FeedRepository,
        private val trackRepository: TrackRepository,
        private val playerRepository: PlayerRepository,
        private val schedulerProvider: SchedulerProvider
) : ViewModel(), LifecycleObserver {

    private val compositeDisposable = CompositeDisposable()
    private var inLike: Boolean = false

    val isLoading = MutableLiveData<Boolean>()

    val feeds: LiveData<PagedList<TrackWithChannel>> by lazy {
        feedRepository.feeds()
    }

    fun onLikeClick(trackData: TrackWithChannel) {
        if (!inLike) {
            trackRepository.like(trackData.track, trackData.isLike)
                    .doOnSubscribe { inLike = true }
                    .doOnComplete { inLike = false }
                    .subscribe()
                    .addTo(compositeDisposable)
        }
    }

    fun refreshFeed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onListen(track: Track) {
        playerRepository.onListen(track)
                .subscribe()
                .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}