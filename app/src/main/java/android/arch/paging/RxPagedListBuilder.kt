package android.arch.paging

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.Executor


@Suppress("UNCHECKED_CAST")
class RxPagedListBuilder<Key, Value>(
        private val initialLoadKey: Key?,
        private val config: PagedList.Config,
        private val boundaryCallback: PagedList.BoundaryCallback<*>,
        private val dataSourceFactory: DataSource.Factory<Key, Value>,
        private val backgroundExecutor: Executor,
        private val mainThreadExecutor: Executor
) {
    private var list: PagedList<Value>? = null
    private val listsSubject = PublishSubject.create<PagedList<Value>>()
    private var dataSource: DataSource<Key, Value>? = null

    private val dataSourceCallback = DataSource.InvalidatedCallback {
        invalidate()
    }

    private fun invalidate() {
        val initializeKey = list?.lastKey as? Key ?: initialLoadKey
        do {
            dataSource?.removeInvalidatedCallback(dataSourceCallback)
            dataSource = dataSourceFactory.create()
            list = PagedList.Builder<Key, Value>(dataSource!!, config)
                    .setBoundaryCallback(boundaryCallback)
                    .setMainThreadExecutor(mainThreadExecutor)
                    .setBackgroundThreadExecutor(backgroundExecutor)
                    .setInitialKey(initializeKey)
                    .build()
        } while (list!!.isDetached)
        listsSubject.onNext(list!!)
    }

    fun build(): Flowable<PagedList<Value>> {
        if (!listsSubject.hasObservers()) {
            backgroundExecutor.execute {
                invalidate()
            }
        }
        return listsSubject.toFlowable(BackpressureStrategy.DROP)
    }
}