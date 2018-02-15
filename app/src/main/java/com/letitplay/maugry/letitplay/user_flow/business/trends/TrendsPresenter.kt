package com.letitplay.maugry.letitplay.user_flow.business.trends

import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView

object TrendsPresenter : BasePresenter<IMvpView>() {

//    var extendTrackList: List<ExtendTrackModel>? = null
//    var extendChannelList: List<Channel>? = null
//    var playlist: List<AudioTrack>? = null
//    var updatedTrack: TrackWithChannel? = null
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
//                            (it.channel to it.feedData).toAudioTrack()
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
//                        if (extendTrack.isLiked == null) {
//                            val isLiked = FavouriteTracksModel(extendTrack.id, true)
//                            TrackManager.updateFavouriteTrack(isLiked)
//                            extendTrack.isLiked = isLiked
//                        } else {
//                            extendTrack.isLiked?.let {
//                                it.isLike = !it.isLike
//                                TrackManager.updateFavouriteTrack(it)
//                            }
//                        }
//                        extendTrack.feedData?.likeCount = it.likeCount
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
//                        extendTrack.feedData?.listenCount = it.listenCount
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