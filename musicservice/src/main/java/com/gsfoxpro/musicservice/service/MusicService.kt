package com.gsfoxpro.musicservice.service

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.Player.STATE_IDLE
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.gsfoxpro.musicservice.ExoPlayerListener
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.ui.MusicPlayerNotification


class MusicService : Service() {

    var mediaSession: MediaSessionCompat? = null
        private set

    var musicRepo: MusicRepo? = null
        set(value) {
            field = value
            notifyRepoChangedListeners(value)
            initTrack(value?.currentAudioTrack)
        }

    private val binder = LocalBinder()
    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest
    private var audioFocusRequested = false
    private var lastInitializedTrack: AudioTrack? = null
    private val metadataBuilder = MediaMetadataCompat.Builder()
    private var becomingNoisyReceiverRegistered = false
    private val updateIntervalMs = 1000L
    private val progressHandler = Handler()
    private var needUpdateProgress = false
    private val repoListeners: MutableSet<RepoChangesListener> = mutableSetOf()
    private var initialPlaybackSpeed: Float = 1f

    private val stateBuilder: PlaybackStateCompat.Builder = PlaybackStateCompat.Builder()
            .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_STOP
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)

    private val playbackSpeed get() = mediaSession?.controller?.playbackState?.playbackSpeed ?: initialPlaybackSpeed

    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onCommand(command: String, extras: Bundle?, cb: ResultReceiver?) {
            when (command) {
                CHANGE_PLAYBACK_SPEED -> {
                    val arg = extras?.getFloat(ARG_SPEED) ?: return
                    val currentState = mediaSession?.controller?.playbackState ?: return
                    exoPlayer.playbackParameters = PlaybackParameters(arg, 1.0f)
                    val newState = stateBuilder.setState(currentState.state, currentState.position, arg).build()
                    mediaSession?.setPlaybackState(newState)
                }
                UPDATE_INFO -> {
                    mediaSession?.setMetadata(metadataBuilder.build())
                    sendPlaylistInfoEvent()
                }
            }
        }

        override fun onPlay() {
            play(musicRepo?.currentAudioTrack)
        }

        override fun onPause() {
            exoPlayer.playWhenReady = false

            stopUpdateProgress()
            unregisterBecomingNoisyReceiver()
            abandonAudioFocus()

            mediaSession?.apply {
                setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, exoPlayer.currentPosition, playbackSpeed).build())
                MusicPlayerNotification.show(this@MusicService, this)
            }
        }

        override fun onStop() {
            exoPlayer.stop()
            lastInitializedTrack = null

            stopUpdateProgress()
            unregisterBecomingNoisyReceiver()
            abandonAudioFocus()

            mediaSession?.apply {
                isActive = false
                setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, playbackSpeed).build())
            }
            MusicPlayerNotification.hide(this@MusicService)
        }

        override fun onSkipToNext() {
            play(musicRepo?.nextAudioTrack)
        }

        override fun onSkipToPrevious() {
            play(musicRepo?.prevAudioTrack)
        }

        override fun onSeekTo(positionMs: Long) {
            exoPlayer.seekTo(positionMs)
        }

        override fun onSkipToQueueItem(id: Long) {
            play(musicRepo?.getAudioTrackAtId(id.toInt()))
        }
    }

    private val playerListener = object : ExoPlayerListener() {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                STATE_ENDED, STATE_IDLE -> {
                    if (musicRepo?.autoPlay == true && playWhenReady) {
                        mediaSessionCallback.onSkipToNext()
                    }
                }
            }
        }
    }

    private val updateProgressTask = Runnable {
        if (needUpdateProgress) {
            val bundle = Bundle().apply {
                putLong(CURRENT_PROGRESS, exoPlayer.currentPosition)
            }
            mediaSession?.sendSessionEvent(PROGRESS_UPDATE_EVENT, bundle)
            startUpdateProgress(true)
        }
    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                mediaSessionCallback.onPause()
            }
        }
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> mediaSessionCallback.onPlay()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaSessionCallback.onPause()
            else -> mediaSessionCallback.onPause()
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setAudioAttributes(audioAttributes)
                    .build()
        }

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON, null, applicationContext, MediaButtonReceiver::class.java)

        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setCallback(mediaSessionCallback)
            setMediaButtonReceiver(PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0))
        }

        exoPlayer = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this), DefaultTrackSelector(), DefaultLoadControl())
        exoPlayer.addListener(playerListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        release()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra(ARG_SPEED)) {
            initialPlaybackSpeed = intent.getFloatExtra(ARG_SPEED, 1.0F)
            exoPlayer.playbackParameters = PlaybackParameters(initialPlaybackSpeed, 1.0f)
        }
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun release() {
        exoPlayer.release()
        mediaSession?.release()
        MusicPlayerNotification.hide(this@MusicService)
    }


    fun addTrackToStart(track: AudioTrack) {
        musicRepo?.addTrackToStart(track)
        repoListeners.forEach {
            it.onRepoChanged(musicRepo)
        }
    }

    fun addTrackToEnd(track: AudioTrack) {
        musicRepo?.addTrackToEnd(track)
        repoListeners.forEach {
            it.onRepoChanged(musicRepo)
        }
    }

    fun removeTrack(id:Int){
        musicRepo?.removeTrack(id)
        repoListeners.forEach {
            it.onRepoChanged(musicRepo)
        }
    }

    private fun initTrack(audioTrack: AudioTrack?) {
        if (audioTrack != null) {
            val mediaSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(applicationContext, "user-agent"))
                    .setExtractorsFactory(DefaultExtractorsFactory())
                    .createMediaSource(Uri.parse(audioTrack.url))

            exoPlayer.prepare(mediaSource)
        }
        mediaSession?.setMetadata(buildMetadata(metadataBuilder, audioTrack))
        lastInitializedTrack = audioTrack
        sendPlaylistInfoEvent()
    }

    private fun buildMetadata(builder: MediaMetadataCompat.Builder, audioTrack: AudioTrack?): MediaMetadataCompat {
        return builder.putString(MediaMetadataCompat.METADATA_KEY_ART_URI, audioTrack?.imageUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, audioTrack?.id?.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, audioTrack?.url)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, audioTrack?.subtitle)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audioTrack?.title)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, audioTrack?.lengthInMs ?: 0)
                .build()
    }

    private fun play(audioTrack: AudioTrack?) {
        if (audioTrack == null) {
            return
        }

        var trackChanged = false
        if (lastInitializedTrack?.id != audioTrack.id) {
            initTrack(audioTrack)
            trackChanged = true
        }

        if (!requestAudioFocus()) {
            return
        }

        val currentPosition = if (trackChanged) 0L else exoPlayer.currentPosition
        mediaSession?.apply {
            isActive = true
            setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, currentPosition, playbackSpeed).build())
            MusicPlayerNotification.show(this@MusicService, this)
        }
        mediaSession?.sendSessionEvent("eee", null)

        registerBecomingNoisyReceiver()

        exoPlayer.playWhenReady = true
        startUpdateProgress()
    }

    @Suppress("DEPRECATION")
    private fun requestAudioFocus(): Boolean {
        if (!audioFocusRequested) {
            audioFocusRequested = true

            val audioFocusResult: Int =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) audioManager.requestAudioFocus(audioFocusRequest)
                    else audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

            if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                return false
            }
        }
        return true
    }

    @Suppress("DEPRECATION")
    private fun abandonAudioFocus() {
        if (!audioFocusRequested) {
            return
        }
        audioFocusRequested = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        } else {
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }

    private fun startUpdateProgress(fromRunnable: Boolean = false) {
        if (!fromRunnable && needUpdateProgress) {
            return
        }
        needUpdateProgress = true
        progressHandler.postDelayed(updateProgressTask, updateIntervalMs)
    }

    private fun stopUpdateProgress() {
        needUpdateProgress = false
        progressHandler.removeCallbacks(updateProgressTask)
    }

    private fun sendPlaylistInfoEvent() {
        val bundle = Bundle().apply {
            putBoolean(HAS_NEXT, musicRepo?.hasNext == true)
            putBoolean(HAS_PREV, musicRepo?.hasPrev == true)
        }
        mediaSession?.sendSessionEvent(PLAYLIST_INFO_EVENT, bundle)
    }

    private fun registerBecomingNoisyReceiver() {
        if (!becomingNoisyReceiverRegistered) {
            registerReceiver(becomingNoisyReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
        }
    }

    private fun unregisterBecomingNoisyReceiver() {
        if (becomingNoisyReceiverRegistered) {
            unregisterReceiver(becomingNoisyReceiver)
        }
    }

    fun addRepoChangesListener(listener: RepoChangesListener) = repoListeners.add(listener)
    fun removeRepoChangesListener(listener: RepoChangesListener) = repoListeners.remove(listener)

    private fun notifyRepoChangedListeners(repo: MusicRepo?) {
        repoListeners.forEach {
            it.onRepoChanged(repo)
        }
    }

    override fun onBind(intent: Intent?) = binder

    inner class LocalBinder(val musicService: MusicService = this@MusicService) : Binder()


    interface RepoChangesListener {
        fun onRepoChanged(repo: MusicRepo?)
    }

    companion object {
        const val UPDATE_INFO = "UPDATE_INFO"
        const val CHANGE_PLAYBACK_SPEED = "CHANGE_PLAYBACK_SPEED"
        const val ARG_SPEED = "ARG_SPEED"
        const val PROGRESS_UPDATE_EVENT = "PROGRESS_UPDATE_EVENT"
        const val CURRENT_PROGRESS = "CURRENT_PROGRESS"
        const val PLAYLIST_INFO_EVENT = "PLAYLIST_INFO_EVENT"
        const val HAS_NEXT = "HAS_NEXT"
        const val HAS_PREV = "HAS_PREV"
    }
}