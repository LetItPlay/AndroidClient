package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
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

    private var likeDisposable: Disposable? = null
    private val repoResult by lazy { feedRepository.feeds(compositeDisposable) }
    private val feeds by lazy { repoResult.pagedList }
    private val noFollowedChannels by lazy {
        channelRepository.followedChannelsId()
                .map(List<Int>::isEmpty)
                .distinctUntilChanged()
                .doOnNext {
                    refreshFeed()
                }
                .toLiveData()
    }
    private val stateMediator = MediatorLiveData<ViewState>()
            .also { mediator ->
                mediator.addSource(feeds, {
                    mediator.setValue(ViewState(it, mediator.value?.noChannels ?: false))
                })
                mediator.addSource(noFollowedChannels, {
                    mediator.setValue(ViewState(mediator.value?.data, it ?: false))
                })
            }

    val state: LiveData<ViewState> by lazy { stateMediator }

    val refreshState by lazy { repoResult.refreshState }

    fun onLikeClick(trackData: TrackWithChannel) {
        if (likeDisposable == null || likeDisposable!!.isDisposed) {
            likeDisposable = trackRepository.like(trackData)
                    .subscribe({}, {
                        Timber.e(it, "Error when liking")
                    })
        }
    }

    fun onSwipeTrackToTop(trackData: TrackWithChannel) {
        trackRepository.swipeTrackToTop(trackData)
                .subscribe({}, {
                    Timber.e(it, "Error when liking")
                })
    }

    fun refreshFeed() {
        repoResult.refresh()
    }

    fun onListen(track: Track) {
        playerRepository.onListen(track)
                .subscribe()
                .addTo(compositeDisposable)
    }
}