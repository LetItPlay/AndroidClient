package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "likes",
        foreignKeys = [
            ForeignKey(
                    entity = Track::class,
                    parentColumns = ["track_id"],
                    childColumns = ["track_id"]
            )
        ]
)
data class Like(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        @ColumnInfo(name = "track_id")
        val trackId: Int
)