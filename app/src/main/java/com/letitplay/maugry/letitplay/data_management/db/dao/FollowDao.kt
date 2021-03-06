package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.Follow
import io.reactivex.Flowable

@Dao
abstract class FollowDao {
    @Query("SELECT * FROM follows")
    abstract fun getAllFollows(): Flowable<List<Follow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFollow(follow: Follow)

    @Query("DELETE FROM follows WHERE follows.channel_id = :channelId")
    abstract fun deleteFollowWithChannelId(channelId: Int)

    @Query("SELECT * FROM follows WHERE follows.channel_id = :id")
    abstract fun getFollow(id: Int): Flowable<List<Follow>>

    @Query("SELECT * FROM follows WHERE follows.channel_id = :id")
    abstract fun getFollowSync(id: Int): Follow?
}