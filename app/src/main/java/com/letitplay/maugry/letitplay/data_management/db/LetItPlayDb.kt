package com.letitplay.maugry.letitplay.data_management.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.letitplay.maugry.letitplay.data_management.db.dao.ChannelDao
import com.letitplay.maugry.letitplay.data_management.db.dao.FollowDao
import com.letitplay.maugry.letitplay.data_management.db.dao.TrackDao
import com.letitplay.maugry.letitplay.data_management.db.dao.TrackWithChannelDao
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Follow
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.Track

@Database(
        version = 1,
        entities = [Channel::class, Track::class, Like::class, Follow::class],
        exportSchema = false
)
@TypeConverters(LanguageConverter::class, TagsConverter::class, DateConverter::class)
abstract class LetItPlayDb : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun channelDao(): ChannelDao
    abstract fun trackWithChannelDao(): TrackWithChannelDao
    abstract fun followDao(): FollowDao
}