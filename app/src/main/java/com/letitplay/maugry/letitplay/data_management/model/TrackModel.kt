package com.letitplay.maugry.letitplay.data_management.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class TrackModel(
        @PrimaryKey
        var id: Long? = null,
        var lang: String? = null,
        @SerializedName("StationID")
        var stationId: Int? = null,
        var title: String? = null,
        var description: String? = null,
        @SerializedName("CoverURL")
        var coverUrl: String? = null,
        @SerializedName("AudioURL")
        var audioUrl: String? = null,
        var totalLengthInSeconds: Int? = null,
        var likeCount: Int? = null,
        var tags: RealmList<String>? = null,
        var listenCount: Int? = null,
        var publishedAt: Date? = null
) : RealmObject()