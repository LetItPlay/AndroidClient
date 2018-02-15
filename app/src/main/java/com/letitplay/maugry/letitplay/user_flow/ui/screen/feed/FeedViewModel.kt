package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.ViewModel
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.FeedRepository
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.disposables.CompositeDisposable


class FeedViewModel(
        private val feedRepository: FeedRepository,
        private val schedulerProvider: SchedulerProvider
) : ViewModel(), LifecycleObserver {

    private val compositeDisposable = CompositeDisposable()

    val feeds by lazy {
        feedRepository.feeds()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .toLiveData()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun onLikeClick(trackData: TrackWithChannel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onTrackClick(trackData: TrackWithChannel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun refreshFeed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}