package com.letitplay.maugry.letitplay.data_management.api.responses

import com.google.gson.annotations.SerializedName
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import java.util.*


data class FeedResponseItem(
        @SerializedName("Id")
        val id: Int,
        @SerializedName("Lang")
        val lang: Language,
        @SerializedName("Title")
        val title: String,
        @SerializedName("Description")
        val description: String?,
        @SerializedName("CoverURL")
        val coverUrl: String?,
        @SerializedName("AudioURL")
        val audioUrl: String?,
        @SerializedName("TotalLengthInSeconds")
        val totalLengthInSeconds: Int,
        @SerializedName("LikeCount")
        val likeCount: Int,
        @SerializedName("Tags")
        val tags: List<String>?,
        @SerializedName("ListenCount")
        val listenCount: Int,
        @SerializedName("PublishedAt")
        val publishedDate: Date,
        @SerializedName("station")
        val channel: Channel
)
