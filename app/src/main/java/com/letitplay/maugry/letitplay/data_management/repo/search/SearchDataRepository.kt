package com.letitplay.maugry.letitplay.data_management.repo.search

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.*
import com.letitplay.maugry.letitplay.data_management.model.SearchResultItem
import com.letitplay.maugry.letitplay.data_management.model.toTrackWithChannels
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.*
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.ReplaySubject

class SearchDataRepository(
        private val api: LetItPlayApi,
        private val postApi: LetItPlayPostApi,
        private val db: LetItPlayDb,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : SearchRepository {
    private val tracksCache: ReplaySubject<List<TrackWithChannel>> = ReplaySubject.create()
    private val channelsCache: ReplaySubject<List<Channel>> = ReplaySubject.create()

    override fun loadTracksAndChannels(): Completable {
        val tracks = api.getTracks()
        val channels = api.channels()
        return Single.zip(tracks, channels, BiFunction { remoteTracks: List<Track>, remoteChannels: List<Channel> ->
            tracksCache.onNext(toTrackWithChannels(remoteTracks, remoteChannels))
            channelsCache.onNext(remoteChannels)
        })
                .toCompletable()
    }

    override fun performQuery(query: String): Flowable<List<SearchResultItem>> {
        val trackQuery: Flowable<List<SearchResultItem>> = tracksCache.toFlowable(BackpressureStrategy.LATEST)
                .compose(localTracksTransformer)
                .map2(query, ::filterTracks)
                .map(::toTrackResult)
                .cache()
        val channelQuery: Flowable<List<SearchResultItem>> = channelsCache.toFlowable(BackpressureStrategy.LATEST)
                .map { it.map { ChannelWithFollow(it, null) } }
                .map2(query, ::filterChannels)
                .map(::toChannelResult)
                .cache()
        val results: Flowable<List<SearchResultItem>> = io.reactivex.rxkotlin.Flowables.combineLatest(trackQuery, channelQuery, db.followDao().getAllFollows(), { tracks, channels, follows ->
            val followsHashMap = follows.associateBy { it.channelId }
            val channelWithFollows = channels.map {
                val channelResult = it as SearchResultItem.ChannelItem
                val updatedChannel = channelResult.channel.copy(followId = followsHashMap[it.channel.channel.id]?.channelId)
                SearchResultItem.ChannelItem(updatedChannel)
            }
            channelWithFollows.plus(tracks)
        })
        return results
                .subscribeOn(schedulerProvider.io())
    }

    private fun toTrackResult(tracks: List<TrackWithChannel>): List<SearchResultItem> =
            tracks.map(SearchResultItem::TrackItem)

    private fun toChannelResult(channels: List<ChannelWithFollow>): List<SearchResultItem> =
            channels.map(SearchResultItem::ChannelItem)

    private fun filterTracks(tracks: List<TrackWithChannel>, query: String): List<TrackWithChannel> =
            tracks.filter {
                val trackObj = it.track
                trackObj.title.like(query) orFalse
                        trackObj.tags?.any { it.like(query) }
            }

    private fun filterChannels(channels: List<ChannelWithFollow>, query: String): List<ChannelWithFollow> =
            channels.filter {
                val channelObj = it.channel
                channelObj.name.like(query) orFalse
                        channelObj.tags?.any { it.like(query) }
            }

    private val localTracksTransformer = FlowableTransformer<List<TrackWithChannel>, List<TrackWithChannel>> {
        it.zipWith(db.likeDao().getAllLikes(),
                BiFunction<List<TrackWithChannel>, List<Like>, List<TrackWithChannel>> { tracks: List<TrackWithChannel>, likes: List<Like> ->
                    val likesHashMap = likes.associateBy { it.trackId }
                    tracks.map {
                        it.copy(likeId = likesHashMap[it.track.id]?.trackId)
                    }
                })
    }

    private fun String.like(expr: String): Boolean = this.contains(expr, true)
}

private fun <T1, T2> Flowable<T1>.map2(b: T2, f: (T1, T2) -> T1) = this.map { f(it, b) }

private infix fun Boolean?.orFalse(other: Boolean?) = (this ?: false) || (other ?: false)