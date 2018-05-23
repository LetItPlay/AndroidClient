package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy


class ChannelPageViewModel(
        private val channelRepository: ChannelRepository
) : BaseViewModel() {

    val channelId = MutableLiveData<Int>()
    val isHidden = MutableLiveData<Boolean>()

    var channel: LiveData<Channel> = Transformations.switchMap(channelId,
            { channelId ->
                channelRepository
                        .channel(channelId)
                        .toLiveData()
            })
    val recentAddedChannelTracks: LiveData<List<Track>> = Transformations.switchMap(channelId, { channelId ->
        channelRepository.recentAddedTracks(channelId)
                .toLiveData()
    })

    fun onFollowClick() {
        channel.value?.let {
            channelRepository
                    .follow(it)
                    .doOnSuccess { channelId.value = it.id }
                    .subscribeBy({})
                    .addTo(compositeDisposable)
        }
    }


    fun onShowClick() {
        channel.value?.let {
            channelRepository.showChannel(it.id)
                    .doOnSuccess {
                        channelId.value = it.id
                    }
                    .subscribeBy({})
                    .addTo(compositeDisposable)
        }
    }


    fun hideChannel(id: Int) {
        channelRepository
                .hideChannel(id)
                .doOnSuccess {
                    channelId.value = it.id
                    isHidden.postValue(true)
                }
                .subscribeBy({})
                .addTo(compositeDisposable)
    }

}