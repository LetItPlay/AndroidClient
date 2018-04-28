package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "channels")
data class Channel(
        @PrimaryKey @ColumnInfo(name = "channel_id")
        val id: Int,
        @ColumnInfo(name = "channel_lang")
        val lang: Language,
        val name: String,
        @SerializedName("Description")
        val channelDescription: String?,
        @SerializedName("ImageURL")
        val imageUrl: String?,
        val subscriptionCount: Int = 0,
        @ColumnInfo(name = "channel_tags")
        val tags: List<String>?
)