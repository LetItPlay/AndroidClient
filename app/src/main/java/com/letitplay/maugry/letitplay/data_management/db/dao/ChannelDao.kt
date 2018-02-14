package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel

@Dao
abstract class ChannelDao {
    @Query("SELECT * FROM channels")
    abstract fun loadAllChannels(): List<Channel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertChannels(channels: List<Channel>)
}