package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class FollowingChannelModel(
        @PrimaryKey
        var id: Int? = null,
        var isFollowing: Boolean = false
) : RealmObject()