package com.letitplay.maugry.letitplay.data_management.repo.profile

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.Optional
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.Response


class ProfileDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : ProfileRepository {
    override fun putIsAdult(): Single<Response<Any>> {
        return api.putAdultContent().subscribeOn(schedulerProvider.io())
    }

    override fun deleteIsAdult(): Single<Response<Any>> {
        return api.deleteAdultContent().subscribeOn(schedulerProvider.io())
    }

    override fun signIn(uuid: String, username: String): Single<Response<Any>> {
        return api.signin(uuid, username).subscribeOn(schedulerProvider.io())
    }


    override fun signUp(uuid: String, username: String): Single<Response<Any>> {
        return api.signup(uuid, username).subscribeOn(schedulerProvider.io())
    }

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