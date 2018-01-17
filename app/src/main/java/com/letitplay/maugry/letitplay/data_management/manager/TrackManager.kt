package com.letitplay.maugry.letitplay.data_management.manager

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.data_management.repo.deleteAll
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.queryAll
import com.letitplay.maugry.letitplay.data_management.repo.saveAll
import com.letitplay.maugry.letitplay.data_management.service.ServiceController
import com.letitplay.maugry.letitplay.utils.toAudioTrack
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


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

    fun getLastTracksWithTag(tag: String) = get(
            local = { TrackModel().query { it.contains("tags", tag) }.sortedByDescending { it.publishedAt } },
            remoteWhen = { true },
            remote = ServiceController.getTracks().map { it.filter { it.tags?.contains(tag) ?: false }.sortedByDescending { it.publishedAt } }
    )

    fun getFavouriteTracks() = get(
            local = { FavouriteTracksModel().queryAll() }
    )

    fun getPieceTracks(id: Int) = get(
            local = { TrackModel().query { it.equalTo("id", id) } }
    )

    fun queryTracks(query: String): Observable<List<AudioTrack>> =
            Observable.zip(ChannelManager.getChannels(),
                    getTracks().map { tracks ->
                        tracks.filter { track ->
                            track.name?.contains(query) or track.description?.contains(query) or track.tags?.contains(query)
                        }
                    },
                    BiFunction { channels: List<ChannelModel>, tracks: List<TrackModel> ->
                        tracks.map { track ->
                            val channel = channels.first { it.id == track.stationId }
                            (channel to track).toAudioTrack()
                        }
                    })

    infix fun Boolean?.or(other: Boolean?) = (other ?: false) || (this ?: false)
}