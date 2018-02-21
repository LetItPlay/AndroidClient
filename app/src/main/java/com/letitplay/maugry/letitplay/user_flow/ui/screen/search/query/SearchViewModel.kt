package com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.model.SearchResultItem
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.data_management.repo.search.SearchRepository
import com.letitplay.maugry.letitplay.utils.ext.toLiveData


class SearchViewModel(
        private val searchRepository: SearchRepository,
        private val channelRepository: ChannelRepository
): ViewModel() {

    val query = MutableLiveData<String>()
    private val submitClicks = MutableLiveData<Any>()
    val searchResult: LiveData<List<SearchResultItem>> = Transformations.switchMap(submitClicks, { newQuery ->
        searchRepository.performQuery(query.value!!)
                .toLiveData()
    })

    fun onChannelFollow(channelWithFollow: ChannelWithFollow) {
        channelRepository.follow(channelWithFollow)
    }

    fun search(query: String) {
        this.query.value = query
        this.submitClicks.postValue(Any())
    }

    fun queryChanged(query: String?) {
        this.query.postValue(query)
    }

    fun onListen(track: AudioTrack) {

    }
}