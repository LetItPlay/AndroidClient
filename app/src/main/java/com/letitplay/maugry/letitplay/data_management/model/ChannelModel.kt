package com.letitplay.maugry.letitplay.data_management.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class ChannelModel(
        @PrimaryKey
        var id: Int? = null,
        var lang: String? = null,
        var name: String? = null,
        @SerializedName("ImageURL")
        var imageUrl: String? = null,
        var subscriptionCount: Int? = null,
        var tags: RealmList<String>? = null
) : RealmObject()