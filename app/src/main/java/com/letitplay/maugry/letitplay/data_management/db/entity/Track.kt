package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.letitplay.maugry.letitplay.data_management.db.LanguageConverter
import java.util.*

@Entity(tableName = "tracks")
data class Track(
        @PrimaryKey
        val id: Int,
        @TypeConverters(LanguageConverter::class)
        val lang: Language,
        @SerializedName("StationID")
        val stationId: Int,
        val title: String,
        val description: String? = null,
        @SerializedName("CoverURL")
        val coverUrl: String? = null,
        @SerializedName("AudioURL")
        val audioUrl: String? = null,
        val totalLengthInSeconds: Int,
        val likeCount: Int,
        val tags: List<String>? = null,
        val listenCount: Int,
        val publishedAt: Date
)