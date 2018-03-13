package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "trackInPlaylist",
        foreignKeys = [
        ForeignKey(
                entity = Track::class,
                parentColumns = ["track_id"],
                childColumns = ["track_id"]
        )
        ])
data class TrackInPlaylist(
        @PrimaryKey
        @ColumnInfo(name = "track_id")
        val trackId: Int,
        @ColumnInfo(name = "track_order")
        val trackOrder: Int
)