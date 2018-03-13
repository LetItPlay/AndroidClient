package com.letitplay.maugry.letitplay.data_management.repo.playlists

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Completable
import io.reactivex.Flowable


interface PlaylistsRepository {
    fun trackInPlaylist(): Flowable<List<TrackWithChannel>>
    fun removeTrackInPlaylist(trackId: Int): Completable
    fun clearPlaylist(): Completable
}