package com.letitplay.maugry.letitplay.data_management.repo.player

import io.reactivex.Completable


interface PlayerRepository {
    fun onListen(trackId: Int): Completable
}