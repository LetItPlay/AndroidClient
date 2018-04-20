package com.letitplay.maugry.letitplay.user_flow

import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.util.Log
import com.gsfoxpro.musicservice.service.MusicService

class AutoMusicService : MusicService() {

    val binder = LocalBinder()

    override fun onLoadChildren(parentId: String,
                                result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        Log.i("DASHAB", "onLoadChildren")
        result.sendResult(createPlayList())
    }

    override fun onGetRoot(clientPackageName: String,
                           clientUid: Int,
                           rootHints: Bundle?): MediaBrowserServiceCompat.BrowserRoot? {
        Log.i("DASHAB", "onGetRoot")
        return BrowserRoot("root", null)

    }

    override fun onBind(intent: Intent?) = super.onBind(intent)

    inner class LocalBinder(val musicService: AutoMusicService = this@AutoMusicService) : Binder()
}