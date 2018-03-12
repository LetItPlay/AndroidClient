package com.letitplay.maugry.letitplay.data_management.repo.track

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackInPlaylist
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toTrackModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction


class TrackDataRepository(
        private val db: LetItPlayDb,
        private val postApi: LetItPlayPostApi,
        private val schedulerProvider: SchedulerProvider
) : TrackRepository {
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
                .doOnSuccess {
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

    override fun swipeTrackToTop(track: TrackWithChannel): Completable {
        val trackId = track.track.id
        val (trackInPlaylistDao, channelDao, trackDao) = Triple(db.playlistDao(), db.channelDao(), db.trackDao())
        val trackInPlaylist = Single.zip(
                Single.fromCallable { db.playlistDao().getTrackInPlaylist(trackId) },
                Single.fromCallable { db.playlistDao().getFirstTrackInPlaylist() },
                BiFunction { trackInPlaylist: TrackInPlaylist?, maxOrder: Int? ->
                    Pair(trackInPlaylist, maxOrder)
                })
        return trackInPlaylist
                .doOnSuccess {
                    val order: Int = when (it.first == null) {
                        true ->
                            when (it.second == null) {
                                true -> 0
                                else -> it.second!! - 1
                            }
                        else -> when (it.first?.order == it.second) {
                            true -> it.second!!
                            else -> it.second!! - 1
                        }
                    }
                    db.runInTransaction {
                        channelDao.insertChannels(listOf(track.channel))
                        trackDao.insertTracks(listOf(track.track))
                        trackInPlaylistDao.insertTrackInPlaylist(TrackInPlaylist(trackId, order))
                    }
                }
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
                .toCompletable()
    }
}