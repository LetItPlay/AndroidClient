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
import io.reactivex.rxkotlin.zipWith

class FeedDataSourceFactory(
        private val api: LetItPlayApi,
        private val db: LetItPlayDb,
        private val preferenceHelper: PreferenceHelper
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
            val endPosition = params.startPosition + params.loadSize
            val needToLoad = endPosition - tracks.size
            if (endPosition < tracks.size) {
                var subList: List<TrackWithChannel> = emptyList()
                synchronized(tracksLock) {
                    subList = tracks.subList(params.startPosition, endPosition)
                }
                callback.onResult(subList.toList())
            } else {
                loadItems(tracks.size, needToLoad)
                        .blockingGet()
                        .let {
                            var subList: List<TrackWithChannel> = emptyList()
                            synchronized(tracksLock) {
                                tracks.addAll(it)
                                val tracksSize = tracks.size
                                subList = tracks.subList(params.startPosition, if (endPosition > tracksSize) tracksSize else endPosition)
                            }
                            callback.onResult(subList.toList())
                        }
            }
        }

        override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<TrackWithChannel>) {
            // Have local data
            if (params.requestedLoadSize < tracks.size) {
                var subList = emptyList<TrackWithChannel>()
                synchronized(tracksLock) {
                    subList = tracks.subList(0, tracks.size)
                }
                callback.onResult(subList.toList(), 0)
            } else {
                loadItems(0, params.requestedLoadSize)
                        .blockingGet()
                        .let {
                            synchronized(tracksLock) {
                                tracks.clear()
                                tracks.addAll(it)
                            }
                            callback.onResult(it.toList(), 0)
                        }
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
    set.forEach {  setItem ->
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