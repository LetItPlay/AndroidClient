package com.letitplay.maugry.letitplay.data_management.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track

@Database(
        entities = [Track::class, Channel::class],
        version = 1,
        exportSchema = false
)
abstract class LetItPlayDb : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun channelDao(): ChannelDao
}