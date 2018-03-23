package com.letitplay.maugry.letitplay.data_management.repo.player

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Completable
import io.reactivex.Flowable


interface PlayerRepository {
    fun onListen(trackId: Int): Completable
}