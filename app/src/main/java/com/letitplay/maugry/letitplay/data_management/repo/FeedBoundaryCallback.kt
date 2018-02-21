package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.paging.PagedList
import android.arch.paging.PagingRequestHelper
import android.support.annotation.MainThread
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.responses.TracksAndChannels
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.ext.joinWithComma
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.Executor

typealias FeedResponse = TracksAndChannels

class FeedBoundaryCallback(
        private val api: LetItPlayApi,
        private val handleNewData: (FeedResponse) -> Unit,
        private val ioExecutor: Executor,
        private val db: LetItPlayDb,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper,
        private val disposableContainer: CompositeDisposable,
        private val networkPageSize: Int
) : PagedList.BoundaryCallback<TrackWithChannel>() {

    private val helper = PagingRequestHelper(ioExecutor)

    private fun insertItemsIntoDb(feedResponse: FeedResponse) {
        ioExecutor.execute {
            handleNewData(feedResponse)
        }
    }

    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            db.channelDao().getFollowedChannelsId()
                    .map(List<Int>::joinWithComma)
                    .flatMap { api.getFeed(it, 0, networkPageSize, preferenceHelper.contentLanguage!!.strValue).toFlowable() }
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(this::insertItemsIntoDb)
                    .addTo(disposableContainer)
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: TrackWithChannel) {
    }

    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: TrackWithChannel) {
    }
}