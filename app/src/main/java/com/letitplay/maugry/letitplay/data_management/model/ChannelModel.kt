package com.letitplay.maugry.letitplay.data_management.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class ChannelModel(
        @PrimaryKey
        var id: Int? = null,
        var lang: String? = null,
        var name: String? = null,
        @SerializedName("image")
        var imageUrl: String? = null,
        @SerializedName("subscription_count")
        var subscriptionCount: Int? = null,
        var tags: String? = null

) : RealmObject()