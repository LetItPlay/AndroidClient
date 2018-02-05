package com.letitplay.maugry.letitplay.data_management.model.remote.responses

import com.google.gson.annotations.SerializedName


data class UpdatedChannelResponse(
        @SerializedName("id")
        var id: Int,
        @SerializedName("lang")
        var lang: String,
        @SerializedName("name")
        var name: String,
        @SerializedName("image")
        var imageUrl: String,
        @SerializedName("subscription_count")
        var subscriptionCount: Int,
        @SerializedName("tags")
        var tags: String,
        @SerializedName("youtube_url")
        var youtubeUrl: String?
)