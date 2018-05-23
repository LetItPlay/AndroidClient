package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy


class ChannelAndCategoriesViewModel(
        private val channelRepo: ChannelRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel(), LifecycleObserver {

    private var followingDisposable: Disposable? = null

    val isLoading = MutableLiveData<Boolean>()

    var listType = MutableLiveData<Int>()

    var channels: MutableLiveData<List<Channel>> = MutableLiveData()

    var catalog: MutableLiveData<Pair<List<Channel>, List<Category>>> = MutableLiveData()

    val refreshing = MutableLiveData<Boolean>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        isLoading.postValue(true)
        refreshChannels()
        refreshCatalog()
    }

    fun onRefreshChannels() {
        refreshing.postValue(true)
        refreshChannels()
        refreshCatalog()
    }

    private fun refreshCatalog() {
        channelRepo.catalog()
                .doOnSuccess { catalog.value = it }
                .doFinally {
                    isLoading.postValue(false)
                    refreshing.postValue(false)
                }
                .subscribeBy({})
                .addTo(compositeDisposable)


    }

    private fun refreshChannels() {
        listType.value?.let {
            channelRepo.channels(it)
                    .doOnSuccess { channels.value = it }
                    .doFinally {
                        refreshing.postValue(false)
                        isLoading.postValue(false)
                    }
                    .subscribeBy({})
                    .addTo(compositeDisposable)
        }
    }

    fun onFollowClick(channelData: Channel) {
        channelRepo.follow(channelData)
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
                .addTo(compositeDisposable)

    }

    fun onShowClick(channelData: Channel) {
        channelRepo.showChannel(channelData.id)
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
                    channels.value = currentList
                }
                .doFinally {
                    isLoading.postValue(false)
                }
                .subscribeBy({})
                .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        followingDisposable?.dispose()
    }
}