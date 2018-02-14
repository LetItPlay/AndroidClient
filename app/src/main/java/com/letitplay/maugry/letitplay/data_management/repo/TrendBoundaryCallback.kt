package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.paging.PagedList
import android.arch.paging.PagingRequestHelper
import android.support.annotation.MainThread
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.responses.TrendResponse
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor


class TrendBoundaryCallback(
        private val api: LetItPlayApi,
        private val handleNewData: (TrendResponse) -> Unit,
        private val ioExecutor: Executor
) : PagedList.BoundaryCallback<TrackWithChannel>() {

    val helper = PagingRequestHelper(ioExecutor)

    private fun insertItemsIntoDb(trendResponse: TrendResponse) {
        ioExecutor.execute {
            handleNewData(trendResponse)
        }
    }

    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            api.trends("ru")
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        insertItemsIntoDb(it)
                    }, {})
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: TrackWithChannel) {
    }

    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: TrackWithChannel) {
//        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
//            api.trends("ru").subscribe({ insertItemsIntoDb(it) }, {})
//        }
    }
}