package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.data_management.repo.deleteAll
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.queryAll
import com.letitplay.maugry.letitplay.data_management.repo.saveAll
import com.letitplay.maugry.letitplay.data_management.service.ServiceController
import io.reactivex.Observable


object TrackManager : BaseManager() {

    fun getTracks() = get(
            local = { TrackModel().queryAll() },
            remote = ServiceController.getTracks(),
            remoteWhen = { true },
            update = { remote ->
                TrackModel().deleteAll()
                remote.saveAll()
            }
    )

    fun updateFavouriteTrack (id:Int, body:LikeModel) = ServiceController.updateFavouriteTracks(id, body)

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

    fun queryTracks(query: String): Observable<List<TrackModel>> = getTracks().map { tracks ->
        tracks.filter { track ->
            track.name?.contains(query) or track.description?.contains(query) or track.tags?.contains(query)
        }
    }

    infix fun Boolean?.or(other: Boolean?) = (other ?: false) || (this ?: false)
}