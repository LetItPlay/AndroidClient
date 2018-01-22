package com.letitplay.maugry.letitplay.data_management.manager

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.data_management.repo.*
import com.letitplay.maugry.letitplay.data_management.service.ServiceController
import com.letitplay.maugry.letitplay.utils.toAudioTrack
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object TrackManager : BaseManager() {

    fun getTracks() = get(
            local = { TrackModel().queryAll() },
            remote = ServiceController.getTracks(),
            remoteWhen = { REMOTE_ALWAYS },
            update = { remote ->
                TrackModel().deleteAll()
                remote.saveAll()
            }
    )

    fun updateFavouriteTrack(id: Int, body: LikeModel) = ServiceController.updateFavouriteTracks(id, body)

    fun getLastTracksWithTag(tag: String) = get(
            local = { ExtendTrackModel().query { it.contains("channel.tags", tag) }.sortedByDescending { it.track?.publishedAt } }
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
            local = { ExtendTrackModel().query { it.equalTo("track.stationId", id) } }
    )

    fun getFavouriteExtendTrack() = get(
            local = { ExtendTrackModel().query { it.equalTo("like.isLiked", true) } }
    )


    fun queryTracks(query: String, contentLanguage: ContentLanguage?): Observable<List<AudioTrack>> =
            Observable.zip(
                    ChannelManager.getExtendChannel()
                            .map { channels ->
                                channels.filter {
                                    val lang = it.channel?.lang?.let { lang -> ContentLanguage.getLanguage(lang) }
                                    contentLanguage == lang
                                }
                            },
                    getExtendTrack()
                            .map { tracks ->
                                tracks.filter {
                                    val track = it.track!!
                                    val lang = it.track?.lang?.let { lang -> ContentLanguage.getLanguage(lang) }
                                    (track.name?.contains(query) or track.description?.contains(query) or track.tags?.contains(query)) && contentLanguage == lang
                                }
                            },
                    BiFunction { channels: List<ExtendChannelModel>, tracks: List<ExtendTrackModel> ->
                        val trackList: MutableList<AudioTrack> = ArrayList()
                        tracks.forEach {
                            val track = it.track!!
                            val channel = channels.firstOrNull { it.id == track.stationId }
                            if (channel != null) {
                                trackList.add((channel.channel!! to track).toAudioTrack())
                            }
                        }
                        return@BiFunction trackList
                    })

    infix fun Boolean?.or(other: Boolean?) = (other ?: false) || (this ?: false)
}