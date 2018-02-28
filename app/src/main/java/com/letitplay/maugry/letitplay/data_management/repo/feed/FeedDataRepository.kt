package com.letitplay.maugry.letitplay.data_management.repo.feed

import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.repo.Listing
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Flowable


class FeedDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper,
        private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE,
        private val prefetchDistance: Int = DEFAULT_PREFETCH_DISTANCE
) : FeedRepository {
    override fun likes(): Flowable<List<Like>> {
        return db.likeDao().getAllLikes(preferenceHelper.contentLanguage!!)
                .subscribeOn(schedulerProvider.io())
    }

    @MainThread
    override fun feeds(): Listing<TrackWithChannel> {
        val pagedListConfig = PagedList.Config.Builder()
                .setPageSize(networkPageSize)
                .setPrefetchDistance(prefetchDistance)
                .setEnablePlaceholders(false)
                .build()
        val dataSourceFactory = FeedDataSourceFactory(api, db, preferenceHelper)

        val livePagedList = LivePagedListBuilder(dataSourceFactory, pagedListConfig)
                .setBackgroundThreadExecutor(schedulerProvider.ioExecutor())
                .setInitialLoadKey(0)
                .build()

        val refreshState = Transformations.switchMap(dataSourceFactory.sourceLiveData, {
            it.initialLoad
        })

        return Listing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(dataSourceFactory.sourceLiveData, {
                    it.networkState
                }),
                retry = {
                    //dataSourceFactory.sourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    dataSourceFactory.invalidateAllData()
                },
                refreshState = refreshState
        )
    }

    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 100
        private const val DEFAULT_PREFETCH_DISTANCE = 10
    }
}