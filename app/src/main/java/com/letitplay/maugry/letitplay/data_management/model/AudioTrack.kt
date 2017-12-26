package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject


open class AudioTrack(
        var id: String? = null,
        var url: String? = null,
        var lenght: String? = null,
        var size: String? = null
) : RealmObject()