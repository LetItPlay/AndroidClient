package com.letitplay.maugry.letitplay.data_management.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import io.reactivex.Flowable

@Dao
abstract class ChannelDao {
    @Query("SELECT * FROM channels WHERE channels.channel_lang = :lang")
    abstract fun getAllChannels(lang: Language): Flowable<List<Channel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateOrInsertChannel(channels: List<Channel>)

    @Query("SELECT * FROM channels WHERE channels.channel_id = :channelId")
    abstract fun getChannel(channelId: Int): Flowable<Channel>

    @Query("SELECT channels.*, follows.channel_id as followId FROM channels " +
            "LEFT JOIN follows ON follows.channel_id = channels.channel_id " +
            "WHERE channels.channel_lang = :lang")
    abstract fun getAllChannelsWithFollow(lang: Language): Flowable<List<ChannelWithFollow>>

    @Query("SELECT channels.*, follows.channel_id as followId FROM channels " +
            "LEFT JOIN follows ON follows.channel_id = channels.channel_id " +
            "WHERE channels.channel_id = :channelId")
    abstract fun getChannelWithFollow(channelId: Int): Flowable<ChannelWithFollow>

    @Query("SELECT channels.channel_id FROM channels " +
            "INNER JOIN follows ON follows.channel_id = channels.channel_id " +
            "WHERE channels.channel_lang = :lang")
    abstract fun getFollowedChannelsId(lang: Language): Flowable<List<Int>>
}