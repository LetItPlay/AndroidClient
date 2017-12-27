package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class AudioTrack(
        @PrimaryKey
        var id: Int? = null,
        var file: String? = null,
        var length_seconds: Int? = null,
        var size_bytes: String? = null
) : RealmObject()