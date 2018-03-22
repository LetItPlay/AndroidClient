package com.letitplay.maugry.letitplay.data_management.repo.profile

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.Optional
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Completable
import io.reactivex.Flowable


class ProfileDataRepository(
        private val db: LetItPlayDb,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
): ProfileRepository {
    override fun changeLanguage(language: Language): Completable {
        return Completable.fromAction {
            val currentLang = preferenceHelper.contentLanguage
            if (currentLang != language)
                preferenceHelper.contentLanguage = language
        }
    }

    override fun getLanguage(): Flowable<Optional<Language>> {
        return preferenceHelper.liveLanguage
    }

    override fun likedTracks(): Flowable<List<TrackWithChannel>> {
        return db.trackWithChannelDao()
                .getLikedTracks(preferenceHelper.contentLanguage!!)
                .subscribeOn(schedulerProvider.io())
    }
}