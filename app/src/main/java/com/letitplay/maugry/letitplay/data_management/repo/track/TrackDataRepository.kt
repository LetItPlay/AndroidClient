package com.letitplay.maugry.letitplay.data_management.repo.track

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackInPlaylist
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.Optional
import io.reactivex.Completable
import io.reactivex.Single


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
                    db.runInTransaction {
                        it.second()
                    }
                }
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
                .toCompletable()
    }

    override fun swipeTrackToTop(track: TrackWithChannel): Completable {
        val trackId = track.track.id
        val (trackInPlaylistDao, channelDao, trackDao) = Triple(db.playlistDao(), db.channelDao(), db.trackDao())
        val trackInPlaylist = Single.fromCallable {
            Pair(Optional.of(db.playlistDao().getTrackInPlaylist(trackId)),
                    Optional.of(db.playlistDao().getFirstTrackInPlaylist()))
        }
        return trackInPlaylist
                .doOnSuccess { (foundTrackInPlaylist, firstOrderInPlaylist) ->
                    val order = calcNewOrder(foundTrackInPlaylist.value, firstOrderInPlaylist.value, Edge.TOP)
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

    override fun swipeTrackToBottom(track: TrackWithChannel): Completable {
        val trackId = track.track.id
        val (trackInPlaylistDao, channelDao, trackDao) = Triple(db.playlistDao(), db.channelDao(), db.trackDao())
        val trackInPlaylist = Single.fromCallable {
            Optional.of(db.playlistDao().getTrackInPlaylist(trackId)) to
                    Optional.of(db.playlistDao().getLastTrackInPlaylist())
        }
        return trackInPlaylist
                .doOnSuccess { (foundTrackInPlaylist, lastOrderInPlaylist) ->
                    val order = calcNewOrder(foundTrackInPlaylist.value, lastOrderInPlaylist.value, Edge.BOTTOM)
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


    companion object {
        fun calcNewOrder(track: TrackInPlaylist?, edgeValue: Int?, edge: Edge): Int {
            return when {
                edgeValue == null -> 0
                track != null && track.trackOrder == edgeValue -> track.trackOrder
                else -> when (edge) {
                    Edge.TOP -> edgeValue - 1
                    Edge.BOTTOM -> edgeValue + 1
                }
            }
        }
    }

    enum class Edge {
        TOP, BOTTOM
    }
}