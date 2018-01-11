package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


class FavouriteTracksModel(
        @PrimaryKey
        var id: Int? = null,
        var likeCounts: Int? = null,
        var isLiked: Boolean = false
) : RealmObject()