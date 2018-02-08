package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class FavouriteTracksModel(
        @PrimaryKey
        var id: Long? = null,
        var isLiked: Boolean = false
) : RealmObject()