package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "follows", foreignKeys = [
    ForeignKey(
            entity = Channel::class,
            parentColumns = ["channel_id"],
            childColumns = ["channelId"]
    )
])
data class Follow(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val channelId: Int
)