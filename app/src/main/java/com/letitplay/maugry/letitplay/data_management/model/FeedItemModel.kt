package com.letitplay.maugry.letitplay.data_management.model


open class FeedItemModel(
        var track: TrackModel? = null,
        var channel: ChannelModel? = null,
        var like: FavouriteTracksModel? = null
)