package com.letitplay.maugry.letitplay.data_management.model

import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel


sealed class SearchResultItem {
    data class TrackItem(val track: TrackWithChannel): SearchResultItem()
    data class ChannelItem(val channel: ChannelWithFollow): SearchResultItem()
}