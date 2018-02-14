package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.repo.ChannelRepository
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class ChannelPageViewModel(
        private val channelRepository: ChannelRepository
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    fun channelPage(channelId: Int): LiveData<Channel> {
        return channelRepository.channel(channelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toLiveData(compositeDisposable)
    }
}