package com.letitplay.maugry.letitplay.data_management.repo.feed

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.PositionalDataSource
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.NetworkState
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import java.io.IOException

abstract class TracksDataSourceFactory(
        protected val preferenceHelper: PreferenceHelper,
        protected val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, TrackWithChannel> {
    protected val tracks = mutableListOf<TrackWithChannel>()
    private val tracksLock = Any()

    val sourceLiveData = MutableLiveData<FeedDataSource>()

    fun invalidateAllData() {
        tracks.clear()
        sourceLiveData.value?.invalidate()
    }

    override fun create(): DataSource<Int, TrackWithChannel> {
        val source = FeedDataSource()
        sourceLiveData.postValue(source)
        return source
    }

    inner class FeedDataSource : PositionalDataSource<TrackWithChannel>() {
        val networkState = MutableLiveData<NetworkState>()
        val initialLoad = MutableLiveData<NetworkState>()

        override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TrackWithChannel>) {
            networkState.postValue(NetworkState.LOADING)
            val lang = preferenceHelper.contentLanguage!!
            val endPosition = params.startPosition + params.loadSize
            val needToLoad = endPosition - tracks.size
            var subList: List<TrackWithChannel> = emptyList()
            try {
                if (endPosition < tracks.size) {
                    synchronized(tracksLock) {
                        subList = tracks.subList(params.startPosition, endPosition)
                    }
                } else {
                    loadItems(tracks.size, needToLoad, lang)
                            .blockingGet()
                            .let {
                                withLock {
                                    tracks.addAll(it)
                                    val tracksSize = tracks.size
                                    subList = tracks.subList(params.startPosition, if (endPosition > tracksSize) tracksSize else endPosition)
                                }
                            }
                }
                callback.onResult(subList.toList())
                networkState.postValue(NetworkState.LOADED)
            } catch (e: IOException) {
                networkState.postValue(NetworkState.error(e.message))
            }
        }

        override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<TrackWithChannel>) {
            val lang = preferenceHelper.contentLanguage!!
            try {
                var subList = emptyList<TrackWithChannel>()
                // Have local data
                if (tracks.isNotEmpty()) {
                    synchronized(tracksLock) {
                        subList = tracks.subList(0, tracks.size)
                    }
                } else {
                    loadItems(0, params.requestedLoadSize, lang)
                            .blockingGet()
                            .let {
                                withLock {
                                    tracks.clear()
                                    tracks.addAll(it)
                                    subList = it
                                }
                            }
                }
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                callback.onResult(subList.toList(), 0)
            } catch (e: Exception) {
                val error = NetworkState.error(e.message)
                networkState.postValue(error)
                initialLoad.postValue(error)
            }
        }
    }

    protected fun <R> withLock(block: () -> R): R {
        synchronized(tracksLock) {
            return block()
        }
    }

    abstract fun loadItems(offset: Int, size: Int, lang: Language?): Maybe<List<TrackWithChannel>>
}