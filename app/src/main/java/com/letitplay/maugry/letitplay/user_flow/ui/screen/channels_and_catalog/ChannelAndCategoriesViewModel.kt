package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog

import android.arch.lifecycle.*
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.Result
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import com.letitplay.maugry.letitplay.utils.toResult
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy


class ChannelAndCategoriesViewModel(
        private val channelRepo: ChannelRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel(), LifecycleObserver {

    private var followingDisposable: Disposable? = null

    val isLoading = MutableLiveData<Boolean>()

    var categorylId: Int? = null

    var channels: MutableLiveData<List<Channel>> = MutableLiveData()

    val catalog: LiveData<Result<Pair<List<Channel>, List<Category>>>> by lazy {
        channelRepo.catalog()
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
        refreshCatalog()
    }

    private fun refreshCatalog() {

    }

    private fun refreshChannels() {
        channelRepo.channels(categorylId)
                .doOnSubscribe { refreshing.postValue(true) }
                .doOnSuccess { channels.value = it }
                .doFinally {
                    refreshing.postValue(false)
                }
                .subscribeBy({})
                .addTo(compositeDisposable)
    }

    fun onFollowClick(channelData: Channel) {
        if (followingDisposable == null || followingDisposable!!.isDisposed) {
            followingDisposable = channelRepo.follow(channelData)
                    .doOnSubscribe {
                        isLoading.postValue(true)
                    }
                    .doOnSuccess {
                        val channel = it
                        val currentList: MutableList<Channel> = channels.value?.toMutableList()
                                ?: mutableListOf()
                        val updatedChannel = currentList.find { it.id == channel.id }
                        val updatedIndex = currentList.indexOf(updatedChannel)
                        currentList.removeAt(updatedIndex)
                        currentList.add(updatedIndex, channel)

                        channels.value = currentList
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