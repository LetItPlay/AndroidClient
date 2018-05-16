package com.letitplay.maugry.letitplay.data_management.repo.track

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.responses.TrackWithEmbeddedChannel
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackInPlaylist
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toTrackWithChannel
import com.letitplay.maugry.letitplay.utils.Optional
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


class TrackDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider
) : TrackRepository {

    override fun report(trackId: Int, reason: Int): Single<TrackWithEmbeddedChannel> {
        return api
                .repotOnTrack(trackId, reason)
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
    }

    override fun like(track: TrackWithChannel): Completable {
        val trackId = track.track.id
        val (likeDao, channelDao, trackDao) = Triple(db.likeDao(), db.channelDao(), db.trackDao())
        val isLiked = Single.fromCallable { db.likeDao().getLikeSync(trackId) != null }
        return isLiked
                .flatMap {
                    val currentIsLiked = it

                    when (currentIsLiked) {
                        true -> api.unLikeTracks(trackId)
                                .map {
                                    it to { likeDao.deleteLikeWithTrackId(trackId) }
                                }

                        else -> api.updateFavouriteTracks(trackId)
                                .map {
                                    it to {
                                        channelDao.updateOrInsertChannel(listOf(track.channel))
                                        trackDao.insertTracks(listOf(track.track))
                                        likeDao.insert(Like(trackId))
                                    }
                                }
                    }
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
                        channelDao.updateOrInsertChannel(listOf(track.channel))
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
                        channelDao.updateOrInsertChannel(listOf(track.channel))
                        trackDao.insertTracks(listOf(track.track))
                        trackInPlaylistDao.insertTrackInPlaylist(TrackInPlaylist(trackId, order))
                    }
                }
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
                .toCompletable()
    }

    override fun trackLikeState(trackId: Int): Flowable<Boolean> {
        return db.likeDao().getLike(trackId)
                .map(List<Like>::isNotEmpty)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun fetchTrack(trackId: Int): Single<TrackWithChannel> {
        return api.getTrackPiece(trackId).map {
            val trackWithChannel = toTrackWithChannel(it)
            trackWithChannel
        }
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
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