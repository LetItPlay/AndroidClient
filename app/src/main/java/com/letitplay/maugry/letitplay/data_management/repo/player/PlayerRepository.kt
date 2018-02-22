package com.letitplay.maugry.letitplay.data_management.repo.player

import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import io.reactivex.Completable


interface PlayerRepository {
    fun onListen(track: Track): Completable
}