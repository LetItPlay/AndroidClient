package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.TrendRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers


class TrendViewModel(
        private val trendRepository: TrendRepository
) : ViewModel() {
    val trends = MutableLiveData<PagedList<TrackWithChannel>>()
    val channels: LiveData<List<Channel>> = MutableLiveData()
    private val disposables = CompositeDisposable()

    init {
        trendRepository.trends()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { trends.value = it }
                .addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}