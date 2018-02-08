package com.letitplay.maugry.letitplay.data_management.manager

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.repo.*
import com.letitplay.maugry.letitplay.data_management.service.ServiceController
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.realm.Sort


object TrackManager : BaseManager() {

    fun getTracks(): Observable<List<TrackModel>> = get(
            local = { TrackModel().queryAll() },
            remoteWhen = { REMOTE_ALWAYS },
            remote = ServiceController.getTracks(),
            update = { remote ->
                remote.saveAll()
            }
    )

    fun updateFavouriteTrack(id: Int, body: UpdateRequestBody) = ServiceController.updateFavouriteTracks(id, body)

    fun getLastTracksWithChannelTag(tag: String) = get(
            local = {
                ExtendTrackModel()
                        .query { sort("track.publishedAt", Sort.DESCENDING) }
                        .filter { extendTrack ->
                            val ret = extendTrack.channel?.tags?.any { it.contains(tag, true) } == true
                            ret
                        }
            }
    )

    fun getFavouriteTracks() = get(
            local = { FavouriteTracksModel().queryAll() }
    )

    fun getListenedTracks() = get(
            local = { ListenedTrackModel().queryAll() }
    )

    fun getExtendTrack() = get(
            local = { ExtendTrackModel().queryAll() }
    )

    fun getPieceExtendTrack(id: Int) = get(
            local = { ExtendTrackModel().query { equalTo("track.stationId", id) } }
    )

    fun getFavouriteExtendTrack() = get(
            local = { ExtendTrackModel().query { equalTo("like.isLiked", true) } }
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
                                tracks.filterLang(contentLanguage)
                                        .filter {
                                            val track = it.track!!
                                            (track.title?.contains(query, true)
                                                    or track.description?.contains(query, true)
                                                    or track.tags?.any { it.contains(query, true) })
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

    private fun List<ExtendTrackModel>.filterLang(lang: ContentLanguage?): List<ExtendTrackModel> {
        return this.filter {
            val trackLanguage = it.track?.lang?.let { lang -> ContentLanguage.getLanguage(lang) }
            trackLanguage == lang
        }
    }

    fun updateExtendTrackModel(extendTrackList: ExtendTrackModel) {
        extendTrackList.save()
    }

    fun updateListenedTrack(listenedTrackModel: ListenedTrackModel) {
        listenedTrackModel.save()
    }

    fun updateFavouriteTrack(favouriteTracksModel: FavouriteTracksModel) {
        favouriteTracksModel.save()
    }
}