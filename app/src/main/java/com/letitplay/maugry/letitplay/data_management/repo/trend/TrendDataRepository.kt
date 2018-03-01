package com.letitplay.maugry.letitplay.data_management.repo.trend

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toTrackWithChannels
import com.letitplay.maugry.letitplay.data_management.repo.switchApplyLikes
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Flowable


class TrendDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : TrendRepository {

    override fun trends(): Flowable<List<TrackWithChannel>> {
        val lang = preferenceHelper.contentLanguage!!
        val likesFlowable = db.likeDao().getAllLikes(lang)
        val trends = api.trends(lang.strValue)
                .map(::toTrackWithChannels)
                .cache()
        return likesFlowable
                .switchApplyLikes(trends)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }
}
