package com.letitplay.maugry.letitplay.data_management.api.responses

import com.google.gson.annotations.SerializedName
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track


data class FeedResponse(
        @SerializedName("Tracks") val tracks: List<Track>? = null,
        @SerializedName("Stations") val channels: List<Channel>? = null
)