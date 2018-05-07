package com.letitplay.maugry.letitplay.data_management.db.entity

import android.arch.persistence.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class Category(
        @ColumnInfo(name = "catalog_id")
        val id: Int,
        val name: String? = null,
        @SerializedName("Stations")
        val channels: List<Channel>? = null
)