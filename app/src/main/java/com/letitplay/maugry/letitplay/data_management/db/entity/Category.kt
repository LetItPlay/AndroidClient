package com.letitplay.maugry.letitplay.data_management.db.entity

import com.google.gson.annotations.SerializedName

data class Category(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("stations")
        val stations: List<Channel>? = null
)