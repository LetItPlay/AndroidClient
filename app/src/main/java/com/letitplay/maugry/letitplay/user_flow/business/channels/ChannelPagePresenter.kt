package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView

object ChannelPagePresenter : BasePresenter<IMvpView>() {

//    var extendTrackList: List<TrackWithChannel>? = null
//    var extendChannel: ExtendChannelModel? = null
//    var updatedChannel: Channel? = null
//    var playlist: List<AudioTrack>? = null
//    var updatedTrack: TrackWithChannel? = null
//
//    val recentTracks: List<TrackWithChannel>
//        get() = extendTrackList?.sortedByDescending { it.publishedAt } ?: emptyList()
//
//    fun loadTracks(id: Int, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
//            ExecutionConfig(
//                    asyncObservable = Observable.zip(
//                            ChannelManager.getChannelPiece(id),
//                            ChannelManager.getFollowingChannelPiece(id),
//                            TrackManager.getTrackPiece(id),
//                            Function3
//                            { channels: List<Channel>, followingChannles: List<FollowingChannelModel>, tracks: List<TrackWithChannel> ->
//                                ChannelPageViewModel(channels.first(), followingChannles.firstOrNull(), tracks)
//                            }),
//                    triggerProgress = false,
//                    onNextNonContext = { model ->
//                        extendTrackList = model.tracks
//                        extendChannel = ExtendChannelModel(model.channel.id, model.channel, model.followingChannel)
//                        playlist = model.tracks.map {
//                            (extendChannel?.channel to it).toAudioTrack()
//                        }
//                    },
//                    onCompleteWithContext = onComplete
//
//            )
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
//    fun updateChannelFollowers(channel: ExtendChannelModel, body: UpdateFollowersRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
//            ExecutionConfig(
//                    asyncObservable = ChannelManager.updateChannelFollowers(channel.id!!, body),
//                    triggerProgress = false,
//                    onNextNonContext = {
//                        updatedChannel = it
//                        if (channel.following == null) {
//                            val following = FollowingChannelModel(channel.id, true)
//                            ChannelManager.updateFollowingChannels(following)
//                            channel.following = following
//                        } else {
//                            channel.following?.let {
//                                it.isFollowing = !it.isFollowing
//                                ChannelManager.updateFollowingChannels(it)
//                            }
//                        }
//                        channel.channel?.subscriptionCount = it.subscriptionCount
//                        ChannelManager.updateExtendChannel(channel)
//                    },
//                    onCompleteWithContext = onComplete
//            )
//    )
//
//    class ChannelPageViewModel(var channel: Channel,
//                               var followingChannel: FollowingChannelModel?,
//                               var tracks: List<TrackWithChannel>
//    )
}