package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.letitplay.maugry.letitplay.data_management.db.LanguageConverter

@Entity(tableName = "channels")
data class Channel(
        @PrimaryKey
        val id: Int,
        @TypeConverters(LanguageConverter::class)
        val lang: Language,
        val name: String,
        @SerializedName("ImageURL")
        val imageUrl: String,
        val subscriptionCount: Int = 0,
        val tags: List<String>?
)