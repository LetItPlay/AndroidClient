package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.OnLifecycleEvent
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.data_management.repo.player.PlayerRepository
import com.letitplay.maugry.letitplay.data_management.repo.track.TrackRepository
import com.letitplay.maugry.letitplay.data_management.repo.trend.TrendRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.Result
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import com.letitplay.maugry.letitplay.utils.toResult
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber


class TrendViewModel(
        private val trendRepository: TrendRepository,
        private val channelRepository: ChannelRepository,
        private val trackRepository: TrackRepository,
        private val playerRepository: PlayerRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel(), LifecycleObserver {
    private var likeDisposable: Disposable? = null
    private var reportDisposable: Disposable? = null

    private val repoResult by lazy { trendRepository.trends(compositeDisposable) }
    val trends by lazy { repoResult.pagedList }
    val loadingState by lazy { repoResult.refreshState }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        refreshChannels()
        refreshTrends()
    }

    private fun refreshTrends() {
        repoResult.refresh()
    }

    private fun refreshChannels() {
        channelRepository.loadChannels()
                .subscribeOn(schedulerProvider.io())
                .subscribe({}, {})
                .addTo(compositeDisposable)
    }

    fun onRefresh() {
        refreshChannels()
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
        if (reportDisposable == null || reportDisposable!!.isDisposed) {
            reportDisposable = trackRepository.report(trackId,reason)
                    .subscribe({}, {
                        Timber.e(it, "Error when liking")
                    })
        }

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