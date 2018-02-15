package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.responses.TrendResponse
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Flowable


class DbTrendRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider
) : TrendRepository {
    override fun trends(): Flowable<PagedList<TrackWithChannel>> {
        val boundaryCallback = TrendBoundaryCallback(api, ::insertNewData, schedulerProvider.ioExecutor())
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
                schedulerProvider.ioExecutor(),
                schedulerProvider.uiExecutor()
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