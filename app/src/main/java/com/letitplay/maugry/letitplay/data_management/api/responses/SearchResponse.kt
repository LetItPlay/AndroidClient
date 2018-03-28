package com.letitplay.maugry.letitplay.data_management.api.responses

import com.google.gson.annotations.SerializedName
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel

data class SearchResponse(
        @SerializedName("totalHits")
        val totalHits: Int,
        @SerializedName("numResults")
        val numResults: Int,
        @SerializedName("results")
        val results: List<SearchResponseItem>
)

sealed class SearchResponseItem {
    data class TrackSearchResponse(val track: TrackWithChannel): SearchResponseItem()
    data class ChannelSearchResponse(val channel: Channel): SearchResponseItem()
}