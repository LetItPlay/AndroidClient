package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import io.reactivex.rxkotlin.addTo


class ChannelPageViewModel(
        private val channelRepository: ChannelRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    private lateinit var channelWithFollow: MutableLiveData<ChannelWithFollow>
    private lateinit var recentAddedChannelTracks: MutableLiveData<List<Track>>

    fun channelPage(channelId: Int): LiveData<ChannelWithFollow> {
        if (!this::channelWithFollow.isInitialized) {
            channelWithFollow = MutableLiveData()
            channelRepository.channel(channelId)
                    .subscribe({
                        channelWithFollow.value = it
                    })
                    .addTo(compositeDisposable)
        }
        return channelWithFollow
    }

    fun onFollowClick() {
        channelWithFollow.value?.let {
            channelRepository.follow(it)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe()
                    .addTo(compositeDisposable)
        }
    }

    fun recentAddedTracks(channelId: Int): LiveData<List<Track>> {
        if (!this::recentAddedChannelTracks.isInitialized) {
            recentAddedChannelTracks = MutableLiveData()
            channelRepository.recentAddedTracks(channelId)
                    .subscribe({
                        recentAddedChannelTracks.value = it
                    })
                    .addTo(compositeDisposable)
        }
        return recentAddedChannelTracks
    }
}