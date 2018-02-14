package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel


@Dao
abstract class TrackWithChannelDao {
    @Query("SELECT tracks.*, channels.*, likes.id FROM tracks " +
            "INNER JOIN channels ON channels.channel_id = tracks.stationId " +
            "LEFT JOIN likes ON likes.track_id = tracks.track_id")
    abstract fun getAllTracks(): DataSource.Factory<Int, TrackWithChannel>
}