package com.letitplay.maugry.letitplay.data_management.api.responses

import com.google.gson.annotations.SerializedName
import com.letitplay.maugry.letitplay.data_management.db.entity.Language


data class UpdatedChannelResponse(
        @SerializedName("id")
        var id: Int,
        @SerializedName("lang")
        var lang: Language,
        @SerializedName("name")
        var name: String,
        @SerializedName("image")
        var imageUrl: String,
        @SerializedName("subscription_count")
        var subscriptionCount: Int,
        @SerializedName("tags")
        var tags: String?,
        @SerializedName("youtube_url")
        var youtubeUrl: String?
)