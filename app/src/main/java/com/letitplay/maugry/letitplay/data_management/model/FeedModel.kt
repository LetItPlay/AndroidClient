package com.letitplay.maugry.letitplay.data_management.model

import com.google.gson.annotations.SerializedName


class FeedModel(
        var tracks: List<TrackModel>? = null,
        @SerializedName("Stations")
        var channels: List<ChannelModel>? = null
)