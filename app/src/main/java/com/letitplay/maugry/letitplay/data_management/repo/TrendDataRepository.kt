package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Completable
import io.reactivex.Flowable


class TrendDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : TrendRepository {

    override fun trends(): Flowable<List<TrackWithChannel>> {
        return db.trackWithChannelDao().getAllTracks(preferenceHelper.contentLanguage!!)
    }

    override fun loadTrends(): Completable {
        return api.trends(if (preferenceHelper.contentLanguage == Language.EN) "en" else "ru" )
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
}