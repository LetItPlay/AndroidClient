package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.data_management.repo.*
import com.letitplay.maugry.letitplay.data_management.service.ServiceController
import io.reactivex.Observable


object TrackManager : BaseManager() {

    fun getTracks() = get(
            local = { TrackModel().queryAll() },
            remote = ServiceController.getTracks(),
            remoteWhen = { local -> local.isEmpty() },
            update = { remote ->
                TrackModel().deleteAll()
                remote.saveAll()
            }
    )

    fun updateFavouriteTrack(id: Int, body: LikeModel) = ServiceController.updateFavouriteTracks(id, body)

    fun getTracksWithTag(tag: String) = get(
            local = { TrackModel().query { it.contains("tags", tag) } },
            remoteWhen = { true },
            remote = ServiceController.getTracks().map { it.filter { it.tags?.contains(tag) ?: false } }
    )

    fun getFavouriteTracks() = get(
            local = { FavouriteTracksModel().queryAll() }
    )

    fun getPieceTracks(id: Int) = get(
            local = { TrackModel().query { it.equalTo("id", id) } }
    )

    fun updateExtendTrackModel(extendTrackList: List<ExtendTrackModel>) {
        ExtendTrackModel().deleteAll()
        extendTrackList.forEach {
            if (it.like == null) {
                val like = FavouriteTracksModel(it.track?.id, it.track?.likeCount, false)
                updateFavouriteTrack(like)
                it.like = like
            }
        }
        extendTrackList.saveAll()
    }

    fun updateFavouriteTrack(like: FavouriteTracksModel) {
        like.save()
    }

    fun getExtendTrack() = get(
            local = { ExtendTrackModel().queryAll() }
    )

    fun getPieceExtendTrack(id: Int) = get(
            local = {ExtendTrackModel().query { it.equalTo("track.stationId", id) }}
    )

    fun getFavouriteExtendTrack() = get(
            local = {ExtendTrackModel().query {it.equalTo("like.isLiked", true)  }}
    )

    fun queryTracks(query: String): Observable<List<TrackModel>> = getTracks().map { tracks ->
        tracks.filter { track ->
            track.name?.contains(query) or track.description?.contains(query) or track.tags?.contains(query)
        }
    }

    infix fun Boolean?.or(other: Boolean?) = (other ?: false) || (this ?: false)
}