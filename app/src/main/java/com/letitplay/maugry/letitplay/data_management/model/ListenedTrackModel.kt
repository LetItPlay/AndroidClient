package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class ListenedTrackModel(
        @PrimaryKey
        var id: Long? = null,
        var isListened: Boolean = false
) : RealmObject()