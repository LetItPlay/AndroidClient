package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.data_management.repo.*
import com.letitplay.maugry.letitplay.data_management.service.ServiceController
import com.letitplay.maugry.letitplay.utils.toAudioTrack
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import com.gsfoxpro.musicservice.model.AudioTrack


object TrackManager : BaseManager() {

    fun getTracks() = get(
            local = { TrackModel().queryAll() },
            remote = ServiceController.getTracks(),
            remoteWhen = {local -> local.isEmpty() },
            update = { remote ->
                TrackModel().deleteAll()
                remote.saveAll()
            }
    )

    fun updateFavouriteTrack(id: Int, body: LikeModel) = ServiceController.updateFavouriteTracks(id, body)

    fun getLastTracksWithTag(tag: String) = get(
            local = { ExtendTrackModel().query { it.contains("track.tags", tag) }.sortedByDescending { it.track?.publishedAt } }
//            remoteWhen = { true },
//            remote = ServiceController.getTracks().map { it.filter { it.tags?.contains(tag) ?: false }.sortedByDescending { it.publishedAt } }
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



    fun queryTracks(query: String): Observable<List<AudioTrack>> =
            Observable.zip(ChannelManager.getExtendChannel(),
                    getExtendTrack().map { tracks ->
                        tracks.filter {
                            val track = it.track!!
                            track.name?.contains(query) or track.description?.contains(query) or track.tags?.contains(query)
                        }
                    },
                    BiFunction { channels: List<ExtendChannelModel>, tracks: List<ExtendTrackModel> ->
                        tracks.map {
                            val track = it.track!!
                            val channel = channels.first { it.id == track.stationId }
                            (channel.channel!! to track).toAudioTrack()
                        }
                    })

    infix fun Boolean?.or(other: Boolean?) = (other ?: false) || (this ?: false)
}