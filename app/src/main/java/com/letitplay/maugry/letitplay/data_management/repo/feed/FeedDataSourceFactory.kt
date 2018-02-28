package com.letitplay.maugry.letitplay.data_management.repo.feed

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.PositionalDataSource
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.NetworkState
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.ext.joinWithComma
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.zipWith
import java.io.IOException

class FeedDataSourceFactory(
        private val api: LetItPlayApi,
        private val db: LetItPlayDb,
        private val preferenceHelper: PreferenceHelper,
        private val compositeDisposable: CompositeDisposable
) : DataSource.Factory<Int, TrackWithChannel> {
    private val tracks = mutableListOf<TrackWithChannel>()
    private val tracksLock = Any()

    val sourceLiveData = MutableLiveData<FeedDataSource>()

    fun invalidateAllData() {
        tracks.clear()
        sourceLiveData.value?.invalidate()
    }

    init {
        db.likeDao().getAllLikes(preferenceHelper.contentLanguage!!)
                .scan(LikesState(), { oldState, newLikesCollection ->
                    LikesState(oldState.new, newLikesCollection.toSet())
                })
                .skip(2) // Skip empty and initial state
                .distinctUntilChanged()
                .doOnNext {
                    val newLikes = it.new - it.old
                    val newDislikes = it.old - it.new
                    synchronized(tracksLock) {
                        updateIfContains(tracks, newLikes, ::isLikeForTrack, ::likeTrack)
                        updateIfContains(tracks, newDislikes, ::isLikeForTrack, ::dislikeTrack)
                    }
                    sourceLiveData.value?.invalidate()
                }
                .subscribe()
                .addTo(compositeDisposable)
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
            val endPosition = params.startPosition + params.loadSize
            val needToLoad = endPosition - tracks.size
            var subList: List<TrackWithChannel> = emptyList()
            try {
                if (endPosition < tracks.size) {
                    synchronized(tracksLock) {
                        subList = tracks.subList(params.startPosition, endPosition)
                    }
                } else {
                    loadItems(tracks.size, needToLoad)
                            .blockingGet()
                            .let {
                                synchronized(tracksLock) {
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
            networkState.postValue(NetworkState.LOADING)
            initialLoad.postValue(NetworkState.LOADING)
            try {
                var subList = emptyList<TrackWithChannel>()
                // Have local data
                if (params.requestedLoadSize < tracks.size) {
                    synchronized(tracksLock) {
                        subList = tracks.subList(0, tracks.size)
                    }
                } else {
                    loadItems(0, params.requestedLoadSize)
                            .blockingGet()
                            .let {
                                synchronized(tracksLock) {
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

    private fun loadItems(offset: Int, size: Int): Maybe<List<TrackWithChannel>> {
        val lang = preferenceHelper.contentLanguage!!
        return db.channelDao().getFollowedChannelsId()
                .map(List<Int>::joinWithComma)
                .firstElement()
                .flatMapSingle {
                    api.getFeed(it, offset, size, lang.strValue)
                }
                .zipWith(db.likeDao().getAllLikes(lang).firstOrError(), { feed, likes ->
                    feed to likes
                })
                .map { (response, likes) ->
                    val likesHashMap = likes.associateBy { it.trackId }
                    val channelHashMap = response.channels!!.associateBy { it.id }
                    response.tracks!!.map {
                        TrackWithChannel(it, channelHashMap[it.stationId]!!, likesHashMap[it.id]?.trackId)
                    }
                }
                .toMaybe()
    }

    inner class LikesState(val old: Set<Like> = emptySet(), val new: Set<Like> = emptySet())
}

private fun <S, T> updateIfContains(collection: MutableList<T>, set: Set<S>, containFilter: ((S, T) -> Boolean), f: ((S, T) -> T)) {
    set.forEach { setItem ->
        val itemIndex = collection.indexOfFirst { containFilter(setItem, it) }
        if (itemIndex != -1) {
            val item = collection[itemIndex]
            collection[itemIndex] = f(setItem, item)
        }
    }
}

private fun isLikeForTrack(like: Like, trackWithChannel: TrackWithChannel): Boolean {
    return like.trackId == trackWithChannel.track.id
}

private fun likeTrack(like: Like, trackWithChannel: TrackWithChannel): TrackWithChannel {
    val trackObj = trackWithChannel.track
    return trackWithChannel.copy(likeId = like.trackId, track = trackObj.copy(likeCount = trackObj.likeCount + 1))
}

private fun dislikeTrack(like: Like, trackWithChannel: TrackWithChannel): TrackWithChannel {
    val trackObj = trackWithChannel.track
    return trackWithChannel.copy(likeId = null, track = trackObj.copy(likeCount = trackObj.likeCount - 1))
}