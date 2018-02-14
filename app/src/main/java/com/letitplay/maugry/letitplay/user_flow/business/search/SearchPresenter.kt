package com.letitplay.maugry.letitplay.user_flow.business.search

import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView


object SearchPresenter : BasePresenter<IMvpView>() {
//    var queryResult: Pair<List<ExtendChannelModel>, List<AudioTrack>> = Pair(emptyList(), emptyList())
//    var lastQuery: String? = null
//    var updatedChannel: Channel? = null
//
//    fun executeQuery(query: String, onComplete: ((IMvpView?) -> Unit)) = execute(
//            ExecutionConfig(
//                    asyncObservable = Observable.zip(
//                            TrackManager.queryTracks(query, currentContentLang?.name?.toLowerCase() ?: "ru"),
//                            ChannelManager.getFollowingChannels(),
//                            BiFunction
//                            { foundedTrackAndChannels: List<Pair<Channel, TrackWithChannel>>, followingChannels: List<FollowingChannelModel> ->
//                                val trackList: List<AudioTrack> = foundedTrackAndChannels.map { it.toAudioTrack() }
//                                val extendChanelsList: List<ExtendChannelModel> = foundedTrackAndChannels.map {
//                                    val stationId = it.first.id
//                                    ExtendChannelModel(stationId, it.first, followingChannels.find { it.id == stationId })
//                                }
//                                extendChanelsList to trackList
//                            }),
//                    onNextNonContext = { queryResult = it },
//                    onCompleteWithContext = onComplete
//            )
//    )
//
//    fun updateChannelFollowers(channel: ExtendChannelModel, body: UpdateFollowersRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
//            ExecutionConfig(
//                    asyncObservable = ChannelManager.updateChannelFollowers(channel.id!!, body),
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
}