package com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.playlists.PlaylistsRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.rxkotlin.addTo


class PlaylistsViewModel(
        private val playlistRepository: PlaylistsRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    data class ViewState(val showTracks: Boolean, val tracks: List<TrackWithChannel>)

    private val tracksInPlaylist: LiveData<List<TrackWithChannel>> by lazy {
        playlistRepository
                .trackInPlaylist()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .toLiveData()
    }

    val state: LiveData<ViewState> = Transformations.map(tracksInPlaylist, { list ->
        ViewState(list.isNotEmpty(), list)
    })

    fun deleteTrack(track: Track) {
        playlistRepository.removeTrackInPlaylist(track.id)
                .subscribe()
                .addTo(compositeDisposable)
    }

    fun clearPlaylist() {
        playlistRepository.clearPlaylist()
                .subscribe()
                .addTo(compositeDisposable)
    }
}
