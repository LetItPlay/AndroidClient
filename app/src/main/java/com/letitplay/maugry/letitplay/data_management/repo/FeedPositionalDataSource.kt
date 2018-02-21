package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PositionalDataSource
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.ext.joinWithComma
import io.reactivex.Maybe


class FeedPositionalDataSource(
        private val api: LetItPlayApi,
        private val db: LetItPlayDb,
        private val preferenceHelper: PreferenceHelper,
        private val schedulerProvider: SchedulerProvider
) : PositionalDataSource<TrackWithChannel>() {

    private var retry: (() -> Any)? = null
    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            schedulerProvider.ioExecutor().execute {
                it.invoke()
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TrackWithChannel>) {
        networkState.postValue(NetworkState.LOADING)
        loadItems(params.startPosition, params.loadSize)
                .subscribe({
                    retry = null
                    callback.onResult(it)
                    networkState.postValue(NetworkState.LOADED)
                }, {
                    retry = { loadRange(params, callback) }
                    networkState.postValue(NetworkState.error(it.message))
                })
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<TrackWithChannel>) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        retry = null

        try {
            loadItems(0, params.pageSize)
                    .doOnError {
                        retry = { loadInitial(params, callback) }
                        val error = NetworkState.error(it.message)
                        networkState.postValue(error)
                        initialLoad.postValue(error)
                    }
                    .doOnSuccess {
                        networkState.postValue(NetworkState.LOADED)
                        initialLoad.postValue(NetworkState.LOADED)
                        callback.onResult(it, 0)
                    }
                    .blockingGet()
        } catch (e: Exception) {

        }
    }

    private fun loadItems(start: Int, size: Int): Maybe<List<TrackWithChannel>> {
        val lang = preferenceHelper.contentLanguage!!
        return db.channelDao().getFollowedChannelsId()
                .map(List<Int>::joinWithComma)
                .firstElement()
                .flatMapSingle {
                    api.getFeed(it, start, size, lang.strValue)
                }
                .map {
                    it to db.likeDao().getAllLikes(lang)
                }
                .map { (response, likes) ->
                    val likesHashMap = likes.associateBy { it.trackId }
                    val channelHashMap = response.channels!!.associateBy { it.id }
                    response.tracks!!.map {
                        TrackWithChannel(it, channelHashMap[it.stationId]!!, likesHashMap[it.id]?.trackId)
                    }
                }
                .toMaybe()
    }

}