package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "catalogs")
data class Catalog(
        @PrimaryKey
        @ColumnInfo(name = "catalog_id")
        val id: Int,
        val name: String?,
        val stations: List<Channel>?
)