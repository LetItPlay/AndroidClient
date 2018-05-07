package com.letitplay.maugry.letitplay.data_management.repo.player

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Completable
import io.reactivex.Single


class PlayerDataRepository(
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : PlayerRepository {
    override fun onListen(trackId: Int): Completable {
        return Single.fromCallable { preferenceHelper.isListened(trackId) }
                .flatMapCompletable {
                    if (!it) {
                        api
                                .updateFavouriteTracks(trackId, UpdateRequestBody.LISTEN)
                                .doOnSuccess {
                                    preferenceHelper.saveListened(trackId)
                                }
                                .toCompletable()
                    } else {
                        Completable.complete()
                    }
                }
                .subscribeOn(schedulerProvider.io())
    }
}