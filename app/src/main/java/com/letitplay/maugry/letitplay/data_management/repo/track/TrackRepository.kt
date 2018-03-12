package com.letitplay.maugry.letitplay.data_management.repo.track

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Completable


interface TrackRepository {
    fun like(track: TrackWithChannel): Completable
    fun swipeTrackToTop(track: TrackWithChannel): Completable
}