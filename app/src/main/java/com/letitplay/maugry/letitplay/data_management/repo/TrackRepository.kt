package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.api.Service
import com.letitplay.maugry.letitplay.data_management.api.responses.TrendResponse
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.model.FeedData
import com.letitplay.maugry.letitplay.data_management.model.toTrackModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


interface TrackRepository {
    fun trends(): Single<List<FeedData>>
}

class TrackRepositoryImpl(
        private val api: Service
) : TrackRepository {
    override fun trends(): Single<List<FeedData>> {
        return api
                .trends("ru")
                .map(TrendResponse::toTracks)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}

internal fun TrendResponse.toTracks(): List<FeedData> {
    if (tracks == null || channels == null)
        return emptyList()
    val channelsHashMap = this.channels.associateBy(Channel::id)
    return this.tracks
            .mapNotNull { track ->
                val channel = channelsHashMap[track.stationId]
                if (channel == null) {
                    null
                } else {
                    FeedData(toTrackModel(track, channel))
                }
            }
}