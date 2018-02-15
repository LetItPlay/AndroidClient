package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.model.toTrackModel
import com.letitplay.maugry.letitplay.utils.Optional
import io.reactivex.Completable


class TrackDataRepository(
        private val db: LetItPlayDb,
        private val postApi: LetItPlayPostApi,
        private val schedulerProvider: SchedulerProvider
): TrackRepository {
    override fun like(track: Track, currentIsLiked: Boolean): Completable {
        val likeDao = db.likeDao()
        val (request, handleLikeDb) = when {
            currentIsLiked -> UpdateRequestBody.UNLIKE to { likeDao.deleteLikeWithTrackId(track.id) }
            else -> UpdateRequestBody.LIKE to { likeDao.insert(Like(track.id)) }
        }
        return postApi.updateFavouriteTracks(track.id, request)
                .map { Optional.of(toTrackModel(it)) }
                .onErrorReturnItem(Optional.none())
                .doOnSuccess {
                    if (it.value != null) {
                        db.runInTransaction {
                            handleLikeDb()
                            db.trackDao().updateTrack(it.value)
                        }
                    }
                }
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
                .toCompletable()
    }
}