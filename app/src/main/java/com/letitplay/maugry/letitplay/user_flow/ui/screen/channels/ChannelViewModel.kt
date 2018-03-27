package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.arch.lifecycle.*
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.Result
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import com.letitplay.maugry.letitplay.utils.toResult
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy


class ChannelViewModel(
        private val channelRepo: ChannelRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel(), LifecycleObserver {

    private var followingDisposable: Disposable? = null

    val isLoading = MutableLiveData<Boolean>()

    val channels: LiveData<Result<List<ChannelWithFollow>>> by lazy {
        channelRepo.channelsWithFollow()
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnEach { isLoading.postValue(false) }
                .toResult(schedulerProvider)
                .toLiveData()
    }

    val refreshing = MutableLiveData<Boolean>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        refreshChannels()
    }

    fun onRefreshChannels() {
        refreshing.postValue(true)
        refreshChannels()
    }

    private fun refreshChannels() {
        channelRepo.loadChannels()
                .doFinally {
                    refreshing.postValue(false)
                }
                .subscribeBy({})
                .addTo(compositeDisposable)
    }

    fun onFollowClick(channelData: ChannelWithFollow) {
        if (followingDisposable == null || followingDisposable!!.isDisposed) {
            followingDisposable = channelRepo.follow(channelData.channel)
                    .doOnSubscribe {
                        isLoading.postValue(true)
                    }
                    .doFinally {
                        isLoading.postValue(false)
                    }
                    .subscribeBy({})
        }
    }

    override fun onCleared() {
        super.onCleared()
        followingDisposable?.dispose()
    }
}