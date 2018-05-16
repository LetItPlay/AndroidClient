package com.letitplay.maugry.letitplay.data_management.repo.track

import com.letitplay.maugry.letitplay.data_management.api.responses.TrackWithEmbeddedChannel
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


interface TrackRepository {
    fun like(track: TrackWithChannel): Completable
    fun report(track: Int, reason: Int): Single<TrackWithEmbeddedChannel>
    fun swipeTrackToTop(track: TrackWithChannel): Completable
    fun swipeTrackToBottom(track: TrackWithChannel): Completable
    fun trackLikeState(trackId: Int): Flowable<Boolean>
    fun fetchTrack(trackId: Int): Single<TrackWithChannel>
}