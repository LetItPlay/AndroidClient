package com.letitplay.maugry.letitplay.data_management.repo.userToken

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Completable


class UserTokenDataRepository(private val db: LetItPlayDb,
                              private val api: LetItPlayApi,
                              private val schedulerProvider: SchedulerProvider,
                              private val preferenceHelper: PreferenceHelper ) : UserTokenRepository {
    override fun getJwt(userToken: String): Completable {

    }
}