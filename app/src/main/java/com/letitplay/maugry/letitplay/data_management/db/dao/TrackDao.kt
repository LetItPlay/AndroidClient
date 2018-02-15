package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Update
import com.letitplay.maugry.letitplay.data_management.db.entity.Track

@Dao
abstract class TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTracks(tracks: List<Track>)

    @Update
    abstract fun updateTrack(track: Track)
}