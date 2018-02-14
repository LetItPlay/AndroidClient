package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.repo.ChannelRepository
import com.letitplay.maugry.letitplay.data_management.repo.TrendRepository
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class TrendViewModel(
        private val trendRepository: TrendRepository,
        private val channelRepository: ChannelRepository
) : ViewModel() {
    private val disposables = CompositeDisposable()

    val trends by lazy {
        trendRepository.trends()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toLiveData(disposables)
    }
    val channels: LiveData<List<Channel>> by lazy {
        channelRepository.channels()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toLiveData(disposables)
    }
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}