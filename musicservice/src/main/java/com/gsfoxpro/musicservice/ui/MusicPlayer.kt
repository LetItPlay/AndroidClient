package com.gsfoxpro.musicservice.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import com.gsfoxpro.musicservice.service.MusicService
import com.gsfoxpro.musicservice.service.MusicService.Companion.ARG_SPEED
import com.gsfoxpro.musicservice.service.MusicService.Companion.CHANGE_PLAYBACK_SPEED
import java.text.SimpleDateFormat
import java.util.*


abstract class MusicPlayer : FrameLayout {

    val TAG = "MusicPlayer"

    var mediaSession: MediaSessionCompat? = null
        set(value) {
            field = value
            if (value == null) unregisterCallback()
            else registerCallback()
        }

    protected val mediaController: MediaControllerCompat?
        get() = mediaSession?.controller

    protected val playing
        get() = mediaController?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING

    protected val pausing
        get() = mediaController?.playbackState?.state == PlaybackStateCompat.STATE_PAUSED

    protected var hasNext = false
    protected var hasPrev = false

    private var seekBar: SeekBar? = null

    private var callbackRegistered = false
    private var needUpdateProgress = false

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            updateButtonsStates()
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            metadata?.let { data ->
                updateTrackInfo(data)
                val duration = data.getLong(METADATA_KEY_DURATION)
                if (duration >= 0) {
                    if (seekBar?.max != duration.toInt()) {
                        seekBar?.progress = 0
                    }
                    seekBar?.max = duration.toInt()
                    updateDuration(duration)
                }
            }
            needUpdateProgress = playing
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            when (event) {
                MusicService.PROGRESS_UPDATE_EVENT -> {
                    val progress = extras?.getLong(MusicService.CURRENT_PROGRESS) ?: 0L
                    if (progress >= 0 && needUpdateProgress) {
                        updateCurrentPosition(progress)
                        seekBar?.progress = progress.toInt()
                    }
                }
                MusicService.PLAYLIST_INFO_EVENT -> {
                    hasNext = extras?.getBoolean(MusicService.HAS_NEXT, false) ?: false
                    hasPrev = extras?.getBoolean(MusicService.HAS_PREV, false) ?: false
                    updateButtonsStates()
                }
            }
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        registerCallback()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        registerCallback()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregisterCallback()
    }

     fun playPause() {
        mediaController?.let { controller ->
            when (playing) {
                true -> controller.transportControls.pause()
                else -> controller.transportControls.play()
            }
        }
    }

    fun next() {
        mediaController?.transportControls?.skipToNext()
    }

    fun prev() {
        mediaController?.transportControls?.skipToPrevious()
    }

    fun stop() {
        mediaController?.transportControls?.stop()
    }

    fun skipToQueueItem(id: Int) {
        mediaController?.transportControls?.skipToQueueItem(id.toLong())
    }

    protected fun initSeekBar(seekBar: SeekBar) {
        this.seekBar = seekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateCurrentPosition(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                needUpdateProgress = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.toLong()?.let { progressMs ->
                    mediaController?.transportControls?.seekTo(progressMs)
                    needUpdateProgress = true
                }
            }
        })

    }

    fun seekTo(ms: Long) {
        seekBar?.progress?.toLong()?.let { progressMs ->
            mediaController?.transportControls?.seekTo(progressMs + ms)
            needUpdateProgress = true
        }
    }

    fun changePlaybackSpeed(value: Float) {
        mediaController?.sendCommand(CHANGE_PLAYBACK_SPEED, Bundle().apply { putFloat(ARG_SPEED, value) }, null)
    }

    abstract fun updateButtonsStates()

    abstract fun updateTrackInfo(metadata: MediaMetadataCompat)

    abstract fun updateDuration(durationMs: Long)

    abstract fun updateCurrentPosition(positionMs: Long)

    @SuppressLint("SimpleDateFormat")
    protected fun getUserFriendlyTime(timeMs: Long): String {
        val formatWithHours = "H:mm:ss"
        val formatWithoutHours = "mm:ss"
        val date = Date(timeMs)
        val cal = Calendar.getInstance()
        cal.time = date
        val hours = cal.get(Calendar.HOUR_OF_DAY)
        val df = SimpleDateFormat(if (hours != 0) formatWithHours else formatWithoutHours)
        return df.format(date)
    }

    private fun unregisterCallback(){
        mediaController?.unregisterCallback(mediaControllerCallback)
        callbackRegistered=false
    }
    private fun registerCallback() {
        if (callbackRegistered) {
            return
        }
        mediaController?.apply {
            registerCallback(mediaControllerCallback)
            callbackRegistered = true
            sendCommand(MusicService.UPDATE_INFO, null, null)
        }
    }
}