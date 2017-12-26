package com.letitplay.maugry.letitplay.data_management.model

import io.realm.RealmObject


open class TrackModel (
        var id: String? = null,
        var lang: String? = null,
        var name: String? = null,
        var image: String? = null,
        var subscription_count: String? = null,
        var tags: String? = null

) : RealmObject()