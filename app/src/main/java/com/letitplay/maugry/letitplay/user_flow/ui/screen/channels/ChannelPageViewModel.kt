package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.repo.ChannelRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers


class ChannelPageViewModel(
        private val channelRepository: ChannelRepository
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

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
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
                    .addTo(compositeDisposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
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