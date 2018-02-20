package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Completable
import io.reactivex.Flowable


class TrendDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val postApi: LetItPlayPostApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : TrendRepository {

    override fun trends(): Flowable<List<TrackWithChannel>> {
        return db.trackWithChannelDao()
                .getAllTracksSortedByDate(preferenceHelper.contentLanguage!!)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun loadTrends(): Completable {
        return api.trends(preferenceHelper.contentLanguage!!.strValue)
                .doOnSuccess { resp ->
                    if (resp.channels == null || resp.tracks == null)
                        return@doOnSuccess
                    db.runInTransaction {
                        db.channelDao().insertChannels(resp.channels)
                        db.trackDao().insertTracks(resp.tracks)
                    }
                }
                .subscribeOn(schedulerProvider.io())
                .toCompletable()
    }

    override fun sendListen(): Completable {
        return Completable.complete()
    }
}