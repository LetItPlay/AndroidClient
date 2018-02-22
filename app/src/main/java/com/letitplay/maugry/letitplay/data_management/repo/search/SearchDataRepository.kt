package com.letitplay.maugry.letitplay.data_management.repo.search

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.SearchResultItem
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction


class SearchDataRepository(
        private val api: LetItPlayApi,
        private val postApi: LetItPlayPostApi,
        private val db: LetItPlayDb,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : SearchRepository {


    override fun performQuery(query: String): Flowable<List<SearchResultItem>> {
        val lang = preferenceHelper.contentLanguage!!
        val rawQuery = "%${query.trim()}%"
        val trackQuery: Flowable<List<SearchResultItem>> = Single.fromCallable<List<TrackWithChannel>>({
            db.trackWithChannelDao().queryTracks(rawQuery, lang)
        })
                .map(::toTrackResult)
                .toFlowable()
                .cache()
        val channelQuery: Flowable<List<SearchResultItem>> = db
                .channelDao()
                .queryChannels(rawQuery, lang)
                .map(::toChannelResult)
                .distinctUntilChanged()
        val results: Flowable<List<SearchResultItem>> = Flowable.combineLatest(trackQuery, channelQuery, BiFunction { tracks, channels ->
            channels.plus(tracks)
        })
        return results
                .subscribeOn(schedulerProvider.io())
    }

    private fun toTrackResult(tracks: List<TrackWithChannel>): List<SearchResultItem> =
            tracks.map(SearchResultItem::TrackItem)

    private fun toChannelResult(channels: List<ChannelWithFollow>): List<SearchResultItem> =
            channels.map(SearchResultItem::ChannelItem)
}