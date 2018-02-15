package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.Like

@Dao
abstract class LikeDao {
    @Insert
    abstract fun insert(like: Like)

    @Query("DELETE FROM likes WHERE likes.track_id = :trackId")
    abstract fun deleteLikeWithTrackId(trackId: Int)
}