package com.letitplay.maugry.letitplay

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.crashlytics.android.Crashlytics
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.data_management.RealmDB
import io.fabric.sdk.android.Fabric
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.Executors


val GL_SCHEDULER_REALM: Scheduler = Schedulers.from(Executors.newSingleThreadExecutor())
var GL_DATA_SERVICE_URL = "https://manage.letitplay.io/api/"
var GL_MEDIA_SERVICE_URL = "https://manage.letitplay.io/uploads/"
val GL_SCHEDULER_IO: Scheduler = Schedulers.from(Executors.newFixedThreadPool(3))
const val GL_PROGRESS_DELAY: Long = 300 // in ms
const val GL_PRESENTER_ACTION_RETRY_DELAY: Long = 300 // in ms
const val GL_PRESENTER_ACTION_RETRY_COUNT: Int = 3 // > 1
const val GL_ALERT_DIALOG_DELAY: Long = 1

class App : Application() {

    private var _musicService: MusicService? = null

    private var isMusicServiceBound: Boolean = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            _musicService = (binder as MusicService.LocalBinder).musicService
            isMusicServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _musicService = null
            isMusicServiceBound = false
        }
    }

    val musicService: MusicService?
        get() = _musicService

    val realmDb = RealmDB

    override fun onCreate() {
        super.onCreate()
        realmDb.init(this)
        bindMusicService()
        Timber.plant(Timber.DebugTree())
        Fabric.with(this, Crashlytics())
    }

    private fun bindMusicService() {
        startService(Intent(this, MusicService::class.java))
        bindService(Intent(applicationContext, MusicService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }
}