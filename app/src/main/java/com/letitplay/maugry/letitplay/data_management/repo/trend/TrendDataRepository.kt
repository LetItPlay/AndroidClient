package com.letitplay.maugry.letitplay.data_management.repo.trend

import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.feedToTrackWithChannels
import com.letitplay.maugry.letitplay.data_management.repo.*
import com.letitplay.maugry.letitplay.data_management.repo.feed.TracksDataSourceFactory
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.zipWith


class TrendDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper,
        private val networkPageSize: Int = DEFAULT_TREND_NETWORK_PAGE_SIZE,
        private val prefetchDistance: Int = DEFAULT_TREND_PREFETCH_DISTANCE
) : TrendRepository {

    @MainThread
    override fun trends(compositeDisposable: CompositeDisposable): Listing<TrackWithChannel> {
        val pagedListConfig = PagedList.Config.Builder()
                .setPageSize(networkPageSize)
                .setPrefetchDistance(prefetchDistance)
                .setEnablePlaceholders(false)
                .build()
        val dataSourceFactory = TrendDataSourceFactory(compositeDisposable)

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
                refresh = {
                    dataSourceFactory.invalidateAllData()
                },
                retry = {},
                refreshState = refreshState
        )
    }

    inner class TrendDataSourceFactory(
            private val compositeDisposable: CompositeDisposable
    ) : TracksDataSourceFactory(preferenceHelper) {

        init {
            db.likeDao().getAllLikes(preferenceHelper.contentLanguage!!)
                    .scan(LikesState(), { oldState, newLikesCollection ->
                        LikesState(oldState.new, newLikesCollection.toSet())
                    })
                    .skip(2) // Skip empty and initial state
                    .distinctUntilChanged()
                    .doOnNext {
                        val newLikes = it.new - it.old
                        val newDislikes = it.old - it.new
                        withLock {
                            updateIfContains(tracks, newLikes, ::isLikeForTrack, ::likeTrack)
                            updateIfContains(tracks, newDislikes, ::isLikeForTrack, ::dislikeTrack)
                        }
                        sourceLiveData.value?.invalidate()
                    }
                    .subscribe()
                    .addTo(compositeDisposable)
        }

        override fun loadItems(offset: Int, size: Int, lang: Language?): Maybe<List<TrackWithChannel>> {
            return Maybe.create { emitter ->
                api.getTrends(offset, size, lang!!.strValue)
                        .zipWith(db.likeDao().getAllLikes(lang).firstOrError(), { feed, likes ->
                            feed to likes
                        })
                        .subscribeOn(schedulerProvider.io())
                        .doFinally(emitter::onComplete)
                        .subscribe({ (response, likes) ->
                            emitter.onSuccess(feedToTrackWithChannels(response, likes))
                        }, { err: Throwable ->
                            emitter.onError(err)
                        })
                        .addTo(compositeDisposable)
            }
        }
    }

    companion object {
        const val DEFAULT_TREND_NETWORK_PAGE_SIZE = 20
        const val DEFAULT_TREND_PREFETCH_DISTANCE = 10
    }
}
