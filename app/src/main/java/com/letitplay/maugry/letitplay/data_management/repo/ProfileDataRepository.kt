package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Flowable


class ProfileDataRepository(
        private val db: LetItPlayDb,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
): ProfileRepository {
    override fun likedTracks(): Flowable<List<TrackWithChannel>> {
        return db.trackWithChannelDao()
                .getLikedTracks(preferenceHelper.contentLanguage!!)
                .subscribeOn(schedulerProvider.io())
    }
}