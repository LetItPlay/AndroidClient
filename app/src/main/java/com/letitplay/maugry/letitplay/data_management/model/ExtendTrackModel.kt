package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class ExtendTrackModel(
        @PrimaryKey
        var id: Long? = null,
        var track: TrackModel? = null,
        var channel: ChannelModel? = null,
        var like: FavouriteTracksModel? = null
) : RealmObject()