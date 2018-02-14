package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import io.reactivex.Flowable

@Dao
abstract class TrackDao {
    @Query("SELECT * FROM tracks")
    abstract fun getAllTracks(): Flowable<List<Track>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTracks(tracks: List<Track>)
}