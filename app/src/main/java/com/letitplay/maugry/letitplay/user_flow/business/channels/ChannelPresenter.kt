package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView


object ChannelPresenter : BasePresenter<IMvpView>() {

//    var extendChannelList: List<ExtendChannelModel>? = null
//
//    var updatedChannel: Channel? = null
//
//    fun loadChannels(triggerProgress: Boolean = true,
//                     onError: ((IMvpView?, Throwable) -> Unit)? = null,
//                     onComplete: ((IMvpView?) -> Unit)? = null) = execute(
//            ExecutionConfig(
//                    triggerProgress = triggerProgress,
//                    asyncObservable = Observable.zip(
//                            ChannelManager.getChannels(),
//                            ChannelManager.getFollowingChannels(),
//                            BiFunction { channels: List<Channel>, followingChannels: List<FollowingChannelModel> ->
//                                Pair(channels, followingChannels)
//                            })
//                            .observeOn(Schedulers.io())
//                            .doOnNext { (channels, followingChannels) ->
//                                extendChannelList = channels
//                                        .map {
//                                            val channelId = it.id
//                                            ExtendChannelModel(it.id, it, followingChannels.find { it.id == channelId }) }
//                                        .filter { it.channel?.lang?.toUpperCase() == currentContentLang?.name }
//                                        .sortedByDescending { it.channel?.subscriptionCount }
//                            },
//                    onErrorWithContext = onError,
//                    onNextNonContext = {},
//                    onCompleteWithContext = onComplete
//
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
}