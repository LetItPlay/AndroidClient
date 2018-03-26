package com.letitplay.maugry.letitplay.data_management.repo.track

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Completable
import io.reactivex.Flowable


interface TrackRepository {
    fun like(track: TrackWithChannel): Completable
    fun swipeTrackToTop(track: TrackWithChannel): Completable
    fun swipeTrackToBottom(track: TrackWithChannel): Completable
    fun trackLikeState(trackId: Int): Flowable<Boolean>
}