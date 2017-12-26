package com.letitplay.maugry.letitplay

import android.app.Application
import com.letitplay.maugry.letitplay.data_management.RealmDB
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.util.concurrent.Executors


val GL_SCHEDULER_REALM: Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
var GL_DATA_SERVICE_URL = "https://manage.letitplay.io/api/"
val GL_SCHEDULER_IO: Scheduler = Schedulers.from(Executors.newFixedThreadPool(3))
const val GL_PROGRESS_DELAY: Long = 300 // in ms
const val GL_PRESENTER_ACTION_RETRY_DELAY: Long = 300 // in ms
const val GL_PRESENTER_ACTION_RETRY_COUNT: Int = 3 // > 1
const val GL_ALERT_DIALOG_DELAY: Long = 1

class App : Application() {

    val realmDb = RealmDB

    override fun onCreate() {
        super.onCreate()
        realmDb.init(this)
    }
}