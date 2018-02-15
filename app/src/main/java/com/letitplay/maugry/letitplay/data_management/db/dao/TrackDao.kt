package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.*
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import io.reactivex.Flowable

@Dao
abstract class TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTracks(tracks: List<Track>)

    @Update
    abstract fun updateTrack(track: Track)

    @Query("SELECT * FROM tracks " +
            "WHERE tracks.stationId = :channelId " +
            "ORDER BY tracks.publishedAt DESC")
    abstract fun getChannelTracksByDate(channelId: Int): Flowable<List<Track>>
}