package com.letitplay.maugry.letitplay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.gsfoxpro.musicservice.service.MusicService
import com.gsfoxpro.musicservice.service.MusicService.Companion.ARG_SPEED
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.fabric.sdk.android.Fabric
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber


const val GL_DATA_SERVICE_URL = "https://beta.api.letitplay.io/"
const val GL_POST_REQUEST_SERVICE_URL = "https://manage.letitplay.io/api/"
const val GL_MEDIA_SERVICE_URL = "https://manage.letitplay.io/uploads/"
const val GL_PROGRESS_DELAY: Long = 300 // in ms


class App : MultiDexApplication() {

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

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.application = this
        ServiceLocator.applicationContext = this
        bindMusicService(PreferenceHelper(this))
        Timber.plant(Timber.DebugTree())
        JodaTimeAndroid.init(this)
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
        ServiceLocator.searchRepository.loadTracksAndChannels()
                .retry()
                .subscribeOn(ServiceLocator.schedulerProvider.io())
                .subscribe()
    }

    private fun bindMusicService(preferenceHelper: PreferenceHelper) {
        startService(buildStartMusicServiceIntent(preferenceHelper))
        bindService(Intent(applicationContext, MusicService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun buildStartMusicServiceIntent(preferenceHelper: PreferenceHelper): Intent {
        return Intent(this, MusicService::class.java).apply {
            putExtra(ARG_SPEED, preferenceHelper.playbackSpeed.value)
        }
    }
}