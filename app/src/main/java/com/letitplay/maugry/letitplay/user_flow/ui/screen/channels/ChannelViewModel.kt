package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.arch.lifecycle.*
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.utils.Result
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import com.letitplay.maugry.letitplay.utils.toResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo


class ChannelViewModel(
        private val channelRepo: ChannelRepository,
        private val schedulerProvider: SchedulerProvider
) : ViewModel(), LifecycleObserver {

    private val compositeDisposable = CompositeDisposable()
    private var inFollow: Boolean = false

    val isLoading = MutableLiveData<Boolean>()

    val channels: LiveData<Result<List<ChannelWithFollow>>> by lazy {
        channelRepo.channelsWithFollow()
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnEach { isLoading.postValue(false) }
                .toResult(schedulerProvider)
                .toLiveData()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        refreshChannels()
    }

    fun onRefreshChannels() {
        refreshChannels()
    }

    private fun refreshChannels() {
        channelRepo.loadChannels()
                .subscribe({}, {})
                .addTo(compositeDisposable)
    }

    fun onFollowClick(channelData: ChannelWithFollow) {
        if (!inFollow) {
            channelRepo.follow(channelData)
                    .doOnSubscribe {
                        isLoading.postValue(true)
                    }
                    .doFinally {
                        isLoading.postValue(false)
                        inFollow = false
                    }
                    .subscribe()
                    .addTo(compositeDisposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}