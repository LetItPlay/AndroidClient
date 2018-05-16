package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.player.PlayerRepository
import com.letitplay.maugry.letitplay.data_management.repo.track.TrackRepository
import com.letitplay.maugry.letitplay.data_management.repo.trend.TrendRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber


class TrendViewModel(
        private val trendRepository: TrendRepository,
        private val trackRepository: TrackRepository,
        private val playerRepository: PlayerRepository
) : BaseViewModel(), LifecycleObserver {
    private var likeDisposable: Disposable? = null
    private var reportDisposable: Disposable? = null

    var isReported = MutableLiveData<Boolean>()

    private val repoResult by lazy { trendRepository.trends(compositeDisposable) }
    val trends by lazy { repoResult.pagedList }
    val loadingState by lazy { repoResult.refreshState }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        refreshTrends()
    }

    private fun refreshTrends() {
        repoResult.refresh()
    }

    fun onRefresh() {
        refreshTrends()
    }

    fun onLikeClick(trackData: TrackWithChannel) {
        if (likeDisposable == null || likeDisposable!!.isDisposed) {
            likeDisposable = trackRepository.like(trackData)
                    .subscribe({}, {
                        Timber.e(it, "Error while liking")
                    })
        }
    }

    fun onReportClick(trackId: Int, reason: Int) {
        trackRepository.report(trackId, reason)
                .doOnSuccess {
                    isReported.postValue(true)
                }
                .subscribeBy({})
                .addTo(compositeDisposable)
    }

    fun onSwipeTrackToTop(trackData: TrackWithChannel) {
        trackRepository.swipeTrackToTop(trackData)
                .subscribe()
                .addTo(compositeDisposable)
    }

    fun onSwipeTrackToBottom(trackData: TrackWithChannel) {
        trackRepository.swipeTrackToBottom(trackData)
                .subscribe()
                .addTo(compositeDisposable)
    }

    fun onListen(track: Track) {
        playerRepository.onListen(track.id)
                .subscribe({}, {})
                .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        likeDisposable?.dispose()
    }
}