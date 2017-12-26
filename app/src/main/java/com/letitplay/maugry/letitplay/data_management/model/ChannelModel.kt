package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class ChannelModel(
        @PrimaryKey
        var id: Int? = null,
        var lang: String? = null,
        var name: String? = null,
        var image: String? = null,
        var subscription_count: Int? = null,
        var tags: String? = null

) : RealmObject()