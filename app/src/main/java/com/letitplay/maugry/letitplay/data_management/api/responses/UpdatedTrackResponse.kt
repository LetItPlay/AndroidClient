package com.letitplay.maugry.letitplay.data_management.api.responses

import com.google.gson.annotations.SerializedName
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import java.util.*

data class AudioFile(
        @SerializedName("id")
        var id: Int,
        @SerializedName("file")
        var filePath: String,
        @SerializedName("length_seconds")
        var lengthInSeconds: Int
)

data class UpdatedTrackResponse(
        @SerializedName("id")
        var id: Int,
        @SerializedName("lang")
        var lang: Language,
        @SerializedName("station")
        var stationId: Int,
        @SerializedName("audio_file")
        var audioFile: AudioFile,
        @SerializedName("name")
        var name: String,
        @SerializedName("description")
        var description: String?,
        @SerializedName("image")
        var imageUrl: String?,
        @SerializedName("like_count")
        var likeCount: Int,
        @SerializedName("tags")
        var tags: String?,
        @SerializedName("listen_count")
        var listenCount: Int,
        @SerializedName("published_at")
        var publishedAt: Date
)