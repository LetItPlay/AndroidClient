package com.letitplay.maugry.letitplay.data_management.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class AudioTrack(
        @PrimaryKey
        var id: Int? = null,
        var fileUrl: String? = null,
        @SerializedName("length_seconds")
        var lengthInSeconds: Int? = null,
        @SerializedName("size_bytes")
        var sizeInBytes: String? = null
) : RealmObject()