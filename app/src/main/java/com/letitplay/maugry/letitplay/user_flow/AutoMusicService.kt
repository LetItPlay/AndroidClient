package com.letitplay.maugry.letitplay.user_flow

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.model.toAudioTrack
import com.letitplay.maugry.letitplay.data_management.repo.playlists.PlaylistsRepository

class AutoMusicService : MusicService() {

    val binder = LocalBinder()
    val MEDIA_ID_ROOT = "ROOT"
    private var mCarConnectionReceiver: BroadcastReceiver? = null
    private var mIsConnectedToCar: Boolean? = null
    var mediaPlaylist: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()

    lateinit var playlistRepo: PlaylistsRepository
    val metadataBuilder = MediaMetadataCompat.Builder()

    override fun onLoadChildren(parentId: String,
                                result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        if (mediaPlaylist.isNotEmpty()) {
            result.sendResult(mediaPlaylist)
        } else {
            result.detach()
            playlistRepo.trackInPlaylist().subscribeOn(ServiceLocator.schedulerProvider.io()).observeOn(ServiceLocator.schedulerProvider.ui()).doOnNext {
                val audioPlayList: MutableList<AudioTrack> = mutableListOf()
                it.forEach {
                    audioPlayList.add(it.toAudioTrack())
                    mediaPlaylist.add(MediaBrowserCompat.MediaItem(buildMetadata(metadataBuilder, it.toAudioTrack()).description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))

                }
                musicRepo = MusicRepo(audioPlayList, false)
                result.sendResult(mediaPlaylist)

            }.subscribe()
        }
    }

    override fun onGetRoot(clientPackageName: String,
                           clientUid: Int,
                           rootHints: Bundle?): MediaBrowserServiceCompat.BrowserRoot? {
        return BrowserRoot(MEDIA_ID_ROOT, null)
    }

    override fun onCreate() {
        playlistRepo = ServiceLocator.playlistsRepository
        super.onCreate()
    }

    private fun registerCarConnectionReceiver() {
        val filter = IntentFilter("com.google.android.gms.car.media.STATUS")
        mCarConnectionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val connectionEvent = intent.getStringExtra("media_connection_status")
                mIsConnectedToCar = "media_connected" == connectionEvent.toLowerCase()
            }
        }
        registerReceiver(mCarConnectionReceiver, filter)
    }

    override fun onBind(intent: Intent?): IBinder =
            when (SERVICE_INTERFACE == intent?.action) {
                true -> super.onBind(intent)
                else -> binder
            }


    inner class LocalBinder(val musicService: AutoMusicService = this@AutoMusicService) : Binder()
}