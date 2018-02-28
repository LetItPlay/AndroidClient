package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import io.reactivex.Flowable

@Dao
abstract class LikeDao {
    @Insert
    abstract fun insert(like: Like)

    @Query("DELETE FROM likes WHERE likes.track_id = :trackId")
    abstract fun deleteLikeWithTrackId(trackId: Int)

    @Query("SELECT * FROM likes " +
            "INNER JOIN tracks ON tracks.track_id = likes.track_id " +
            "WHERE tracks.track_lang = :lang")
    abstract fun getAllLikes(lang: Language): Flowable<List<Like>>

    @Query("SELECT * FROM likes " +
            "INNER JOIN tracks ON tracks.track_id = likes.track_id")
    abstract fun getAllLikes(): Flowable<List<Like>>
}