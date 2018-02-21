package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.paging.PositionalDataSource
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.responses.FeedResponse
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.ext.joinWithComma
import io.reactivex.Maybe


class FeedPositionalDataSource(
        private val api: LetItPlayApi,
        private val db: LetItPlayDb,
        private val preferenceHelper: PreferenceHelper
) : PositionalDataSource<TrackWithChannel>() {

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TrackWithChannel>) {
        loadItems(params.startPosition, params.loadSize)
                .subscribe {
                    callback.onResult(it)
                }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<TrackWithChannel>) {
        loadItems(0, params.pageSize)
                .blockingGet()
                .let {
                    callback.onResult(it, 0)
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
                .onErrorReturnItem(FeedResponse(emptyList(), emptyList()))
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