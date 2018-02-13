package com.letitplay.maugry.letitplay.data_management.manager


object TrackManager : BaseManager() {

//    fun getTracks(): Observable<List<Track>> = get(
//            local = { Track().queryAll() },
//            remoteWhen = { REMOTE_ALWAYS },
//            remote = ServiceController.getTracks(),
//            update = { remote ->
//                Track().deleteAll()
//                remote.saveAll()
//            }
//    )
//
//    fun getTrackPiece(idStation: Int) = ServiceController.getChannelTracks(idStation)
//
//    fun updateFavouriteTrack(id: Int, body: UpdateRequestBody) = ServiceController.updateFavouriteTracks(id, body)
//
//    fun getFavouriteTracks() = get(
//            local = { FavouriteTracksModel().queryAll() }
//    )
//
//    fun getListenedTracks() = get(
//            local = { ListenedTrackModel().queryAll() }
//    )
//
//    fun getFavouriteExtendTrack() = get(
//            local = { ExtendTrackModel().query { equalTo("like.isLiked", true) } }
//    )
//
//
//    fun queryTracks(query: String, contentLanguage: String): Observable<List<Pair<Channel, Track>>> =
//            Observable.zip(
//                    ChannelManager.getChannels()
//                            .map { channels ->
//                                channels.filter { it.lang == contentLanguage }
//                            },
//                    getTracks()
//                            .map { tracks ->
//                                tracks.filter { it.lang == contentLanguage }
//                                        .filter {
//                                            val track = it
//                                            (it.title?.contains(query, true)
//                                                    or track.description?.contains(query, true)
//                                                    or track.tags?.any { it.contains(query, true) })
//                                        }
//                            },
//                    BiFunction { channels: List<Channel>, tracks: List<Track> ->
//                        val trackList: MutableList<Pair<Channel, Track>> = ArrayList()
//                        tracks.forEach {
//                            val track = it
//                            val channel = channels.firstOrNull { it.id == track.stationId }
//                            if (channel != null) {
//                                trackList.add(channel to track)
//                            }
//                        }
//                        return@BiFunction trackList
//                    })
//
//    infix fun Boolean?.or(other: Boolean?) = (other ?: false) || (this ?: false)
//
//    fun updateExtendTrackModel(extendTrackList: ExtendTrackModel) {
//        extendTrackList.save()
//    }
//
//    fun updateListenedTrack(listenedTrackModel: ListenedTrackModel) {
//        listenedTrackModel.save()
//    }
//
//    fun updateFavouriteTrack(favouriteTracksModel: FavouriteTracksModel) {
//        favouriteTracksModel.save()
//    }
}