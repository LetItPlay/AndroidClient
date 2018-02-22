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
        val trackQuery: Flowable<List<SearchResultItem>> = Flowable.unsafeCreate<List<TrackWithChannel>> {
            val tracks = db.trackWithChannelDao().queryTracks(rawQuery, lang)
            it.onNext(tracks)
            it.onComplete()
        }
                .map(::toTrackResult)
                .subscribeOn(schedulerProvider.io())
        val channelQuery: Flowable<List<SearchResultItem>> = db
                .channelDao()
                .queryChannels(rawQuery, lang)
                .map(::toChannelResult)
                .subscribeOn(schedulerProvider.io())
        return Flowable.combineLatest(trackQuery, channelQuery, BiFunction { tracks, channels ->
            channels.plus(tracks)
        })
    }

    private fun toTrackResult(tracks: List<TrackWithChannel>): List<SearchResultItem> =
            tracks.map(SearchResultItem::TrackItem)

    private fun toChannelResult(channels: List<ChannelWithFollow>): List<SearchResultItem> =
            channels.map(SearchResultItem::ChannelItem)
}