package com.letitplay.maugry.letitplay.data_management

import android.app.Application
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import io.realm.Realm
import io.realm.RealmConfiguration


object RealmDB {

    fun init(application: Application) {
        Realm.init(application)
        RealmConfiguration.Builder()
                .name("letitplay.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .modules(Realm.getDefaultModule())
                .initialData { realm ->
//                    realm.insert(
//                            mutableListOf(
//                                    ChannelModel(
//                                            id = "0",
//                                            lang = "ru",
//                                            name = "name",
//                                            image = "image_value",
//                                            subscription_count = "1",
//                                            tags = ""
//                                    )
//                            )
//                    )
                }
                .build()
                .let { Realm.setDefaultConfiguration(it) }
    }
}