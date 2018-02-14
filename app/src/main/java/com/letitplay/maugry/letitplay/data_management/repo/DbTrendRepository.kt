package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.responses.TrendResponse
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import java.util.concurrent.Executor


class DbTrendRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val ioExecutor: Executor
) : TrendRepository {
    override fun trends(): LiveData<PagedList<TrackWithChannel>> {
        val boundaryCallback = TrendBoundaryCallback(api, ::insertNewData, ioExecutor)
        val dataSourceFactory = db.trackWithChannelDao().getAllTracks()
        val builder = LivePagedListBuilder(dataSourceFactory, 1000)
                .setBoundaryCallback(boundaryCallback)
        return builder.build()
    }

    private fun insertNewData(trends: TrendResponse) {
        if (trends.channels != null && trends.tracks != null) {
            db.runInTransaction {
                db.channelDao().insertChannels(trends.channels)
                db.trackDao().insertTracks(trends.tracks)
            }
        }
    }
}