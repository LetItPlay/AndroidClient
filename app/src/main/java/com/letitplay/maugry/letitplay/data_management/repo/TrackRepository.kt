package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import io.reactivex.Completable


interface TrackRepository {
    fun like(track: Track, currentIsLiked: Boolean): Completable
}