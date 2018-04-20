package com.letitplay.maugry.letitplay.user_flow.ui.screen.global

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.data_management.repo.track.TrackRepository
import com.letitplay.maugry.letitplay.user_flow.AutoMusicService
import com.letitplay.maugry.letitplay.user_flow.ui.utils.InitializedMutableLiveData
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import java.lang.ref.WeakReference


class PlayerViewModel(
        application: Application,
        private val trackRepository: TrackRepository,
        private val channelRepository: ChannelRepository
): AndroidViewModel(application) {

    private var musicService: WeakReference<AutoMusicService>? = null
    private val compositeDisposable = CompositeDisposable()

    val currentTrack = MutableLiveData<AudioTrack?>()
    val musicRepo = MutableLiveData<MusicRepo>()
    val currentTrackIsLiked: LiveData<Boolean?> = Transformations.switchMap(currentTrack, { track ->
        if (track != null) {
            trackRepository.trackLikeState(track.id).toLiveData()
        } else {
            InitializedMutableLiveData<Boolean?>(null)
        }
    })
    val currentChannelIsFollow: LiveData<Boolean?> = Transformations.switchMap(currentTrack, { track ->
        if (track != null) {
            channelRepository.channelFollowState(track.channelId).toLiveData()
        } else {
            InitializedMutableLiveData<Boolean?>(null)
        }
    })

    private val tracksInRepo: MutableLiveData<List<TrackWithChannel>> = MutableLiveData()

    private val mediaControllerCallback = object: MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            val currentTrackInRepo = musicRepo.value?.currentAudioTrack
            if (currentTrackInRepo?.id != currentTrack.value?.id)
                currentTrack.postValue(currentTrackInRepo)
        }
    }

    fun setMusicService(musicService: AutoMusicService?) {
        musicService?.let {
            this.musicService = WeakReference(it)
            it.mediaSession?.controller?.registerCallback(mediaControllerCallback)
        }
    }

    fun setMusicRepo(musicRepo: MusicRepo?, tracks: List<TrackWithChannel>) {
        this.musicRepo.value = musicRepo
        this.tracksInRepo.value = tracks
    }

    fun likeCurrentTrack() {
        val currentTrackId = currentTrack.value?.id
        val track = tracksInRepo.value?.firstOrNull { it.track.id == currentTrackId }
        if (track != null) {
            trackRepository.like(track)
                    .subscribeBy(::onError)
                    .addTo(compositeDisposable)
        }
    }

    fun followChannelForCurrentTrack() {
        val currentTrackId = currentTrack.value?.id
        val channel = tracksInRepo.value?.firstOrNull { it.track.id == currentTrackId }?.channel
        if (channel != null) {
            channelRepository.follow(channel)
                    .subscribeBy(::onError)
                    .addTo(compositeDisposable)
        }
    }

    fun fetchAndPlay(trackId: Int, cb: (TrackWithChannel) -> Unit) {
        trackRepository.fetchTrack(trackId)
                .doOnSuccess {
                    cb(it)
                }
                .subscribe()
                .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        musicService = null
    }

    private fun onError(throwable: Throwable) {
        Timber.e("Error: ", throwable)
    }
}