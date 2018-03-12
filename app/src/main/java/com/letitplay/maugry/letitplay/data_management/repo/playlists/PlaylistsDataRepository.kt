package com.letitplay.maugry.letitplay.data_management.repo.playlists

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Flowable


class PlaylistsDataRepository(private val db: LetItPlayDb,
                              private val schedulerProvider: SchedulerProvider) : PlaylistsRepository {

    override fun trackInPlaylist(): Flowable<List<TrackWithChannel>> {
        return db.trackWithChannelDao().getTracksInPlaylist() .subscribeOn(schedulerProvider.io())
    }
}