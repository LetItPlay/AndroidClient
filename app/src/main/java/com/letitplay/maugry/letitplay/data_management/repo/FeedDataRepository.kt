package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.responses.FeedResponse
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.Flowable


class FeedDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider
) : FeedRepository {
    override fun feeds(): Flowable<PagedList<TrackWithChannel>> {
        val boundaryCallback = FeedBoundaryCallback(api, ::insertNewData, schedulerProvider.ioExecutor())
        val dataSourceFactory = db.trackWithChannelDao().getAllTracksWithFollowedChannels()

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

    private fun insertNewData(trends: FeedResponse) {
        if (trends.channels != null && trends.tracks != null) {
            db.runInTransaction {
                db.channelDao().insertChannels(trends.channels)
                db.trackDao().insertTracks(trends.tracks)
            }
        }
    }
}