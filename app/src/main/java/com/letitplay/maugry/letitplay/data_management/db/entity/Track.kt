package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "tracks",
        foreignKeys = [
            ForeignKey(
                    entity = Channel::class,
                    parentColumns = ["channel_id"],
                    childColumns = ["stationId"]
            )
        ]
)
data class Track(
        @PrimaryKey @ColumnInfo(name = "track_id")
        val id: Int,
        @ColumnInfo(name = "track_lang")
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
        @ColumnInfo(name = "track_tags")
        val tags: List<String>? = null,
        val listenCount: Int,
        val publishedAt: Date
)