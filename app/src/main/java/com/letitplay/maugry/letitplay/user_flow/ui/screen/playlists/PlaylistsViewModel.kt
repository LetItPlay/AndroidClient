package com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.playlists.PlaylistsRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.ext.toLiveData


class PlaylistsViewModel(
        private val playlistRepository: PlaylistsRepository,
        private val schedulerProvider: SchedulerProvider

) : BaseViewModel(), LifecycleObserver {

    val tracksInPlaylist: LiveData<List<TrackWithChannel>> by lazy {
        playlistRepository
                .trackInPlaylist()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .toLiveData()
    }
}