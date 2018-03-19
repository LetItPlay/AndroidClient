package com.letitplay.maugry.letitplay.data_management.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.letitplay.maugry.letitplay.data_management.db.dao.*
import com.letitplay.maugry.letitplay.data_management.db.entity.*

@Database(
        version = 1,
        entities = [Channel::class, Track::class, Like::class, Follow::class, TrackInPlaylist::class, UserToken::class],
        exportSchema = false
)
@TypeConverters(LanguageConverter::class, TagsConverter::class, DateConverter::class)
abstract class LetItPlayDb : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun channelDao(): ChannelDao
    abstract fun trackWithChannelDao(): TrackWithChannelDao
    abstract fun followDao(): FollowDao
    abstract fun likeDao(): LikeDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun userTokenDao(): UserTokenDao
}