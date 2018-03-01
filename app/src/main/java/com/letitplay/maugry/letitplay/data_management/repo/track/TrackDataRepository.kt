package com.letitplay.maugry.letitplay.data_management.repo.track

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toTrackModel
import io.reactivex.Completable
import io.reactivex.Single


class TrackDataRepository(
        private val db: LetItPlayDb,
        private val postApi: LetItPlayPostApi,
        private val schedulerProvider: SchedulerProvider
): TrackRepository {
    override fun like(track: TrackWithChannel): Completable {
        val trackId = track.track.id
        val (likeDao, channelDao, trackDao) = Triple(db.likeDao(), db.channelDao(), db.trackDao())
        val isLiked = Single.fromCallable { db.likeDao().getLike(trackId) != null }
        return isLiked
                .flatMap {
                    val currentIsLiked = it
                    val (request, handleLikeDb) = when {
                        currentIsLiked -> UpdateRequestBody.UNLIKE to { likeDao.deleteLikeWithTrackId(trackId) }
                        else -> UpdateRequestBody.LIKE to {
                            channelDao.insertChannels(listOf(track.channel))
                            trackDao.insertTracks(listOf(track.track))
                            likeDao.insert(Like(trackId))
                        }
                    }
                    postApi.updateFavouriteTracks(trackId, request)
                            .map { it to handleLikeDb }
                }
                .map {
                    val trackModel = toTrackModel(it.first)
                    db.runInTransaction {
                        it.second()
                        db.trackDao().updateTrack(trackModel)
                    }
                }
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
                .toCompletable()
    }
}