package com.letitplay.maugry.letitplay.data_management.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class TrackModel(
        @PrimaryKey
        var id: Long? = null,
        var state: String? = null,
        var lang: String? = null,
        @SerializedName("review_failed_reason")
        var reviewFailedReason: String? = null,
        @SerializedName("station")
        var stationId: Int? = null,
        @SerializedName("audio_file")
        var audio: AudioTrack? = null,
        var name: String? = null,
        var url: String? = null,
        var description: String? = null,
        var image: String? = null,
        @SerializedName("like_count")
        var likeCount: Int? = null,
        @SerializedName("report_count")
        var reportCount: Int? = null,
        var tags: String? = null,
        @SerializedName("listen_count")
        var listenCount: Int? = null,
        @SerializedName("published_at")
        var publishedAt: Date? = null

) : RealmObject()