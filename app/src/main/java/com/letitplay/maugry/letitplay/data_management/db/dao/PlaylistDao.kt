package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackInPlaylist

@Dao
abstract class PlaylistDao {
    @Insert
    abstract fun insertTrackInPlaylist(trackInPlaylist: TrackInPlaylist)

    @Query("SELECT * FROM trackInPlaylist " +
            "WHERE trackInPlaylist.track_id = :id")
    abstract fun getTrackInPlaylist(id: Int): TrackInPlaylist?
}