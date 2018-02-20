package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.responses.FeedResponse
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.Result
import com.letitplay.maugry.letitplay.utils.toResult
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable


class FeedDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper,
        private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE
) : FeedRepository {

    private val requestDisposable = CompositeDisposable()

    override fun feeds(): Flowable<Result<PagedList<TrackWithChannel>>> {
        val boundaryCallback = FeedBoundaryCallback(
                api,
                ::insertNewData,
                schedulerProvider.ioExecutor(),
                db,
                schedulerProvider,
                preferenceHelper,
                requestDisposable,
                networkPageSize
        )
        val dataSourceFactory = db.trackWithChannelDao()
                .getAllTracksWithFollowedChannelsSortedByDate(preferenceHelper.contentLanguage!!)

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
                .subscribeOn(schedulerProvider.io())
                .toResult(schedulerProvider)
                .doFinally {
                    requestDisposable.dispose()
                }
    }

    private fun insertNewData(trends: FeedResponse) {
        if (trends.channels != null && trends.tracks != null) {
            db.runInTransaction {
                db.channelDao().insertChannels(trends.channels)
                db.trackDao().insertTracks(trends.tracks)
            }
        }
    }

    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 10
    }
}