package com.letitplay.maugry.letitplay.data_management.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.model.Track

@Dao
abstract class TrackDao {
    @Query("SELECT * FROM tracks")
    abstract fun loadAllTracks(): List<Track>
    @Insert
    abstract fun insertTracks(tracks: List<Track>)
}