package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Completable
import io.reactivex.Single


class PlayerDataRepository(
        private val postApi: LetItPlayPostApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : PlayerRepository {
    override fun onListen(track: Track): Completable {
        return Single.fromCallable { preferenceHelper.isListened(track.id) }
                .flatMapCompletable {
                    if (!it) {
                        postApi
                                .updateFavouriteTracks(track.id, UpdateRequestBody.LISTEN)
                                .doOnSuccess {
                                    preferenceHelper.saveListened(track.id)
                                }
                                .toCompletable()
                    } else {
                        Completable.complete()
                    }
                }
                .subscribeOn(schedulerProvider.io())
    }
}