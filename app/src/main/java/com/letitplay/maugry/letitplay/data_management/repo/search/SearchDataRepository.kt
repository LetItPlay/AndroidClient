package com.letitplay.maugry.letitplay.data_management.repo.search

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.responses.SearchResponse
import com.letitplay.maugry.letitplay.data_management.api.responses.SearchResponseItem
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.SearchResultItem
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.functions.BiFunction

class SearchDataRepository(
        private val api: LetItPlayApi,
        private val db: LetItPlayDb,
        private val schedulerProvider: SchedulerProvider
) : SearchRepository {

    override fun performQuery(query: String): Flowable<List<SearchResultItem>> {
        val trimmedQuery = query.toLowerCase().trim()
        val searchFlowable = api.search(trimmedQuery)
                .map(SearchResponse::results)
        val trackQuery = searchFlowable.map {
            it.filterIsInstance(SearchResponseItem.TrackSearchResponse::class.java)
                    .map(SearchResponseItem.TrackSearchResponse::track)
        }
                .toFlowable()
                .compose(localTracksTransformer)
                .map(::toTrackResult)
                .cache()
        val channelQuery = searchFlowable.map {
            it.filterIsInstance(SearchResponseItem.ChannelSearchResponse::class.java)
                    .map(SearchResponseItem.ChannelSearchResponse::channel)
                    .map { ChannelWithFollow(it, null) }
        }
                .toFlowable()
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

    private val localTracksTransformer = FlowableTransformer<List<TrackWithChannel>, List<TrackWithChannel>> {
        it.zipWith(db.likeDao().getAllLikes(),
                BiFunction<List<TrackWithChannel>, List<Like>, List<TrackWithChannel>> { tracks: List<TrackWithChannel>, likes: List<Like> ->
                    val likesHashMap = likes.associateBy { it.trackId }
                    tracks.map {
                        it.copy(likeId = likesHashMap[it.track.id]?.trackId)
                    }
                })
    }
}