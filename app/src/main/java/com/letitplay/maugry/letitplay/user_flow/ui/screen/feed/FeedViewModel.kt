package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.arch.lifecycle.LifecycleObserver
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.feed.FeedRepository
import com.letitplay.maugry.letitplay.data_management.repo.player.PlayerRepository
import com.letitplay.maugry.letitplay.data_management.repo.track.TrackRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import io.reactivex.rxkotlin.addTo


class FeedViewModel(
        private val feedRepository: FeedRepository,
        private val trackRepository: TrackRepository,
        private val playerRepository: PlayerRepository
) : BaseViewModel(), LifecycleObserver {
    private var inLike: Boolean = false
    private val repoResult by lazy { feedRepository.feeds() }

    val feeds by lazy { repoResult.pagedList }

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
        repoResult.refresh()
    }

    fun onListen(track: Track) {
        playerRepository.onListen(track)
                .subscribe()
                .addTo(compositeDisposable)
    }
}