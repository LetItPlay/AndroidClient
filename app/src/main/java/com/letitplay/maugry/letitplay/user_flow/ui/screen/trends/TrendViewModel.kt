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
import io.reactivex.rxkotlin.addTo


class TrendViewModel(
        private val trendRepository: TrendRepository,
        private val channelRepository: ChannelRepository,
        private val trackRepository: TrackRepository,
        private val playerRepository: PlayerRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel(), LifecycleObserver {
    private var inLike: Boolean = false

    val trends by lazy {
        trendRepository.trends()
                .toResult(schedulerProvider)
                .toLiveData()
    }

    val channels: LiveData<Result<List<Channel>>> by lazy {
        channelRepository.channels()
                .toResult(schedulerProvider)
                .toLiveData()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        refreshChannels()
        refreshTrends()
    }

    private fun refreshTrends() {
        trendRepository.loadTrends()
                .subscribe({}, {})
                .addTo(compositeDisposable)
    }

    private fun refreshChannels() {
        channelRepository.loadChannels()
                .subscribe({}, {})
                .addTo(compositeDisposable)
    }

    fun onRefresh() {
        // TODO
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

    fun onListen(track: Track) {
        playerRepository.onListen(track)
                .subscribe()
                .addTo(compositeDisposable)
    }
}