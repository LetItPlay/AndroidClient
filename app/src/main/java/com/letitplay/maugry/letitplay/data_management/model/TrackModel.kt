package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class TrackModel(
        @PrimaryKey
        var id: Int? = null,
        var state: String? = null,
        var lang: String? = null,
        var review_failed_reason: String? = null,
        var station: Int? = null,
        var audio_file: AudioTrack? = null,
        var name: String? = null,
        var url: String? = null,
        var descriptio: String? = null,
        var image: String? = null,
        var like_count: Int? = null,
        var report_count: Int? = null,
        var tags: String? = null,
        var listen_count: Int? = null,
        var published_at: String? = null

) : RealmObject()