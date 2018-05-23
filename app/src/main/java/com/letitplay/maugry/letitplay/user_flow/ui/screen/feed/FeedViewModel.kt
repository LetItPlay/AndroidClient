package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.data_management.repo.feed.FeedRepository
import com.letitplay.maugry.letitplay.data_management.repo.player.PlayerRepository
import com.letitplay.maugry.letitplay.data_management.repo.track.TrackRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber


class FeedViewModel(
        private val feedRepository: FeedRepository,
        private val trackRepository: TrackRepository,
        private val channelRepository: ChannelRepository,
        private val playerRepository: PlayerRepository
) : BaseViewModel(), LifecycleObserver {

    data class ViewState(
            val data: PagedList<TrackWithChannel>?,
            val noChannels: Boolean = false
    )

    var isReported = MutableLiveData<Boolean>()
    var likeDisposable: Disposable? = null
    val repoResult by lazy { feedRepository.feeds(compositeDisposable) }
    val feeds by lazy { repoResult.pagedList }
    val refreshState by lazy { repoResult.refreshState }


    fun onLikeClick(trackData: TrackWithChannel) {
        if (likeDisposable == null || likeDisposable!!.isDisposed) {
            likeDisposable = trackRepository.like(trackData)
                    .subscribe({}, {
                        Timber.e(it, "Error when liking")
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

    fun refreshFeed() {
        repoResult.refresh()
    }

    fun onListen(track: Track) {

    }
}