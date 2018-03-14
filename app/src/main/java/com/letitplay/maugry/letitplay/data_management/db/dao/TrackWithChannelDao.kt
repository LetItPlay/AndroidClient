package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Flowable


@Dao
abstract class TrackWithChannelDao {
    @Query("SELECT tracks.*, channels.*, likes.track_id as likeId FROM tracks " +
            "INNER JOIN channels ON channels.channel_id = tracks.stationId " +
            "INNER JOIN likes ON likes.track_id = tracks.track_id " +
            "WHERE tracks.track_lang = :lang")
    abstract fun getLikedTracks(lang: Language): Flowable<List<TrackWithChannel>>

        @Query("SELECT * FROM tracks " +
            "INNER JOIN channels ON channels.channel_id = tracks.stationId "+
            "INNER JOIN trackInPlaylist ON trackInPlaylist.track_id = tracks.track_id " +
            "ORDER BY trackInPlaylist.track_order ")
    abstract fun getTracksInPlaylist(): Flowable<List<TrackWithChannel>>
}