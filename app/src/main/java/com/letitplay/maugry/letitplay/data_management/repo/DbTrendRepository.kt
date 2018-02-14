package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.responses.TrendResponse
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Flowable
import java.util.concurrent.Executor


class DbTrendRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val ioExecutor: Executor,
        private val mainThreadExecutor: Executor
) : TrendRepository {
    override fun trends(): Flowable<PagedList<TrackWithChannel>> {
        val boundaryCallback = TrendBoundaryCallback(api, ::insertNewData, ioExecutor)
        val dataSourceFactory = db.trackWithChannelDao().getAllTracks()

        val pagedListConfig = PagedList.Config.Builder()
                .setPageSize(20)
                .setPrefetchDistance(50)
                .setEnablePlaceholders(false)
                .build()
        val rxPagedListBuilder = RxPagedListBuilder(
                null,
                pagedListConfig,
                boundaryCallback,
                dataSourceFactory,
                ioExecutor,
                mainThreadExecutor
        )
        return rxPagedListBuilder.build()
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