package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers


object ChannelPresenter : BasePresenter<IMvpView>() {

    var extendChannelList: List<ExtendChannelModel>? = null

    var updatedChannel: ChannelModel? = null

    fun loadChannels(triggerProgress: Boolean = true,
                     onError: ((IMvpView?, Throwable) -> Unit)? = null,
                     onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    triggerProgress = triggerProgress,
                    asyncObservable = Observable.zip(
                            ChannelManager.getChannels(),
                            ChannelManager.getFollowingChannels(),
                            BiFunction { channels: List<ChannelModel>, followingChannels: List<FollowingChannelModel> ->
                                Pair(channels, followingChannels)
                            })
                            .observeOn(Schedulers.io())
                            .doOnNext { (channels, followingChannels) ->
                                extendChannelList = channels
                                        .map {
                                            val channelId = it.id
                                            ExtendChannelModel(it.id, it, followingChannels.find { it.id == channelId }) }
                                        .filter { it.channel?.lang?.toUpperCase() == currentContentLang?.name }
                                        .sortedByDescending { it.channel?.subscriptionCount }
                            },
                    onErrorWithContext = onError,
                    onNextNonContext = {},
                    onCompleteWithContext = onComplete

            )
    )

    fun updateChannelFollowers(channel: ExtendChannelModel, body: UpdateFollowersRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.updateChannelFollowers(channel.id!!, body),
                    triggerProgress = false,
                    onNextNonContext = {
                        updatedChannel = it
                        if (channel.following == null) {
                            val following = FollowingChannelModel(channel.id, true)
                            ChannelManager.updateFollowingChannels(following)
                            channel.following = following
                        } else {
                            channel.following?.let {
                                it.isFollowing = !it.isFollowing
                                ChannelManager.updateFollowingChannels(it)
                            }
                        }
                        channel.channel?.subscriptionCount = it.subscriptionCount
                        ChannelManager.updateExtendChannel(channel)
                    },
                    onCompleteWithContext = onComplete
            )
    )
}