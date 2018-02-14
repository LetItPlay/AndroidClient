package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.*
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import io.reactivex.Flowable

@Dao
abstract class ChannelDao {
    @Query("SELECT * FROM channels")
    abstract fun getAllChannels(): Flowable<List<Channel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertChannels(channels: List<Channel>)

    @Query("SELECT * FROM channels WHERE channels.channel_id = :channelId")
    abstract fun getChannel(channelId: Int): Flowable<Channel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertChannel(channel: Channel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateChannel(channel: Channel)
}