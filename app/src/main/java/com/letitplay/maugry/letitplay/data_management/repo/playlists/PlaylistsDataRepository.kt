package com.letitplay.maugry.letitplay.data_management.repo.playlists

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Completable
import io.reactivex.Flowable


class PlaylistsDataRepository(private val db: LetItPlayDb,
                              private val schedulerProvider: SchedulerProvider) : PlaylistsRepository {
    override fun clearPlaylist(): Completable {
        return Completable.fromAction { db.playlistDao().deleteAll() }
                .subscribeOn(schedulerProvider.io())
    }

    override fun removeTrackInPlaylist(trackId: Int): Completable {
        return Completable.fromAction { db.playlistDao().delete(trackId) }
                .subscribeOn(schedulerProvider.io())
    }

    override fun trackInPlaylist(): Flowable<List<TrackWithChannel>> {
        return db.trackWithChannelDao().getTracksInPlaylist().subscribeOn(schedulerProvider.io())
    }
}