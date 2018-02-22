package com.letitplay.maugry.letitplay.data_management.repo.feed

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.PreferenceHelper

class FeedDataSourceFactory(
        private val api: LetItPlayApi,
        private val db: LetItPlayDb,
        private val preferenceHelper: PreferenceHelper,
        private val schedulerProvider: SchedulerProvider
): DataSource.Factory<Int, TrackWithChannel> {
    init {
        db.likeDao().getAllLikes(preferenceHelper.contentLanguage!!)
                .skip(1)
                .distinctUntilChanged()
                .doOnNext {
                    sourceLiveData.value?.invalidate()
                }
                .subscribeOn(schedulerProvider.io())
                .subscribe()
    }

    val sourceLiveData = MutableLiveData<FeedPositionalDataSource>()

    override fun create(): DataSource<Int, TrackWithChannel> {
        val source = FeedPositionalDataSource(api, db, preferenceHelper, schedulerProvider)
        sourceLiveData.postValue(source)
        return source
    }
}