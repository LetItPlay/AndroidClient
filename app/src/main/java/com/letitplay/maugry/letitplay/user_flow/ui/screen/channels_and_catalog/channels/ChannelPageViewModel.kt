package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber


class ChannelPageViewModel(
        private val channelRepository: ChannelRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    val channelId = MutableLiveData<Int>()

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
        }

    }


    private fun onError(throwable: Throwable) {
        Timber.e("Error: ", throwable)
    }
}