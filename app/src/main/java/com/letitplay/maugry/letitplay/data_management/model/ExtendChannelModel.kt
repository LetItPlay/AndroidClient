package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class ExtendChannelModel(
        @PrimaryKey
        var id: Int? = null,
        var channel: ChannelModel? = null,
        var following: FollowingChannelModel? = null
) :
        RealmObject()