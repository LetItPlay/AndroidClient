package com.letitplay.maugry.letitplay.data_management.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel

@Dao
abstract class TrackDao {
    @Query("SELECT * FROM tracks, channels WHERE tracks.stationId = channels.id")
    abstract fun loadAllTracks(): List<TrackWithChannel>
    @Insert
    abstract fun insertTracks(tracks: List<Track>)
}