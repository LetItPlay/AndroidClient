package com.letitplay.maugry.letitplay.user_flow.business.trends

import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView

object TrendsPresenter : BasePresenter<IMvpView>() {

//    var extendTrackList: List<ExtendTrackModel>? = null
//    var extendChannelList: List<Channel>? = null
//    var playlist: List<AudioTrack>? = null
//    var updatedTrack: Track? = null
//
//    fun loadTracksAndChannels(
//            triggerProgress: Boolean = true,
//            onError: ((IMvpView?, Throwable) -> Unit)? = null,
//            onComplete: ((IMvpView?) -> Unit)? = null) = execute(
//            ExecutionConfig(
//                    triggerProgress = triggerProgress,
//                    asyncObservable = Observable.zip(
//                            FeedManager.getTrends(currentContentLang?.name?.toLowerCase() ?: "ru"),
//                            ChannelManager.getChannels(),
//                            TrackManager.getFavouriteTracks(),
//                            ChannelManager.getFollowingChannels(),
//                            TrackManager.getListenedTracks(),
//                            Function5
//                            { feed: FeedModel, channels: List<Channel>,
//                              favouriteTracks: List<FavouriteTracksModel>, followingChannels: List<FollowingChannelModel>, listenedTracks: List<ListenedTrackModel> ->
//                                TrendsViewModel(feed, channels, favouriteTracks, followingChannels, listenedTracks)
//                            })
//                            .observeOn(Schedulers.io())
//                            .doOnNext { model ->
//                                extendTrackList = model.feed.tracks?.map {
//                                    val stationId = it.stationId
//                                    val trackId = it.id
//                                    ExtendTrackModel(it.id, it, model.feed.channels?.find { it.id == stationId },
//                                            model.favouriteTrack.find { it.id == trackId },
//                                            model.listenedTracks.find { it.id == trackId })
//                                }
//
//                                extendChannelList = model.channels.filter { it.lang?.toUpperCase() == currentContentLang?.name }.sortedByDescending { it.subscriptionCount }
//                            },
//                    onErrorWithContext = onError,
//                    onNextNonContext = {
//                        playlist = extendTrackList?.map {
//                            (it.channel to it.track).toAudioTrack()
//                        }
//                    },
//                    onCompleteWithContext = onComplete
//            )
//    )
//
//    fun updateFavouriteTracks(id: Int, extendTrack: ExtendTrackModel, body: UpdateRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
//            ExecutionConfig(
//                    asyncObservable = TrackManager.updateFavouriteTrack(id, body),
//                    triggerProgress = false,
//                    onNextNonContext = {
//                        updatedTrack = it
//                        if (extendTrack.like == null) {
//                            val like = FavouriteTracksModel(extendTrack.id, true)
//                            TrackManager.updateFavouriteTrack(like)
//                            extendTrack.like = like
//                        } else {
//                            extendTrack.like?.let {
//                                it.isLiked = !it.isLiked
//                                TrackManager.updateFavouriteTrack(it)
//                            }
//                        }
//                        extendTrack.track?.likeCount = it.likeCount
//                        TrackManager.updateExtendTrackModel(extendTrack)
//                    },
//                    onCompleteWithContext = onComplete
//            )
//
//    )
//
//    fun updateListenersTracks(extendTrack: ExtendTrackModel, body: UpdateRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
//            ExecutionConfig(
//                    asyncObservable = TrackManager.updateFavouriteTrack(extendTrack.id?.toInt()!!, body),
//                    triggerProgress = false,
//                    onNextNonContext = {
//                        updatedTrack = it
//                        val listened = ListenedTrackModel(extendTrack.id, true)
//                        TrackManager.updateListenedTrack(listened)
//                        extendTrack.listened = listened
//                        extendTrack.track?.listenCount = it.listenCount
//                        TrackManager.updateExtendTrackModel(extendTrack)
//                    },
//                    onCompleteWithContext = onComplete
//            )
//    )
//
//    class TrendsViewModel(var feed: FeedModel,
//                          var channels: List<Channel>,
//                          var favouriteTrack: List<FavouriteTracksModel>,
//                          var followingChannels: List<FollowingChannelModel>,
//                          var listenedTracks: List<ListenedTrackModel>
//    )

}