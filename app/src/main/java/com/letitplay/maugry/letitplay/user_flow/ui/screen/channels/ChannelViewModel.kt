package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.repo.ChannelRepository
import com.letitplay.maugry.letitplay.user_flow.Router
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers


class ChannelViewModel(
        private val channelRepo: ChannelRepository,
        private val router: Router
): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val channels: LiveData<List<ChannelWithFollow>> by lazy {
        channelRepo.channelsWithFollow()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toLiveData(compositeDisposable)
    }

    fun onFollowClick(channelData: ChannelWithFollow) {
        channelRepo.follow(channelData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
                .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun onChannelClick(channel: Channel) {
        router.navigateTo(ChannelPageKey(channel.id))
    }
}