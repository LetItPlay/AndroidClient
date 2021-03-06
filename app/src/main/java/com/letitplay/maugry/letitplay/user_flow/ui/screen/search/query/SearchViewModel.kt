package com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.model.SearchResultItem
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.data_management.repo.player.PlayerRepository
import com.letitplay.maugry.letitplay.data_management.repo.search.SearchRepository
import com.letitplay.maugry.letitplay.data_management.repo.track.TrackRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.ext.toLiveData
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber


class SearchViewModel(
        private val trackRepository: TrackRepository,
        private val searchRepository: SearchRepository,
        private val channelRepository: ChannelRepository,
        private val playerRepository: PlayerRepository
) : BaseViewModel() {

    private var reportDisposable: Disposable? = null
    private val submitClicks = MutableLiveData<Any>()
    val query = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    val searchResult: LiveData<List<SearchResultItem>> = Transformations.switchMap(submitClicks, { _ ->
        searchRepository.performQuery(query.value!!)
                .doOnSubscribe { isLoading.postValue(true) }
                .doOnEach { isLoading.postValue(false) }
                .toLiveData()
    })

    fun onChannelFollow(channelWithFollow: ChannelWithFollow) {
        channelRepository.follow(channelWithFollow.channel)
                .doOnSubscribe { isLoading.postValue(true) }
                .doFinally { isLoading.postValue(false) }
                .subscribe()
                .addTo(compositeDisposable)
    }

    fun search(query: String) {
        this.query.value = query
        this.submitClicks.postValue(Any())
    }

    fun queryChanged(query: String?) {
        this.query.postValue(query)
    }

    fun onReportClick(trackId: Int, reason: Int) {
        if (reportDisposable == null || reportDisposable!!.isDisposed) {
            reportDisposable = trackRepository.report(trackId,reason)
                    .subscribe({}, {
                        Timber.e(it, "Error when liking")
                    })
        }
    }

    fun onListen(track: AudioTrack) {
        playerRepository.onListen(track.id)
                .subscribe({}, {})
                .addTo(compositeDisposable)
    }
}