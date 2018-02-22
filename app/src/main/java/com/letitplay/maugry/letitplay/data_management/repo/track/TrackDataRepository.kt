package com.letitplay.maugry.letitplay.data_management.repo.track

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toTrackModel
import com.letitplay.maugry.letitplay.utils.Optional
import io.reactivex.Completable


class TrackDataRepository(
        private val db: LetItPlayDb,
        private val postApi: LetItPlayPostApi,
        private val schedulerProvider: SchedulerProvider
): TrackRepository {
    override fun like(track: TrackWithChannel): Completable {
        val currentIsLiked = track.isLike
        val trackId = track.track.id
        val (likeDao, channelDao, trackDao) = Triple(db.likeDao(), db.channelDao(), db.trackDao())
        val (request, handleLikeDb) = when {
            currentIsLiked -> UpdateRequestBody.UNLIKE to { likeDao.deleteLikeWithTrackId(trackId) }
            else -> UpdateRequestBody.LIKE to {
                channelDao.insertChannels(listOf(track.channel))
                trackDao.insertTracks(listOf(track.track))
                likeDao.insert(Like(trackId))
            }
        }
        return postApi.updateFavouriteTracks(trackId, request)
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