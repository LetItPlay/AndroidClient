package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object ChannelPresenter : BasePresenter<IMvpView>() {

    var channelList: List<ChannelModel>? = null

    var followingfChannelList: List<FollowingChannelModel>? = null

    var channelModel: ChannelModel? = null

    fun loadChannels(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.getChannels(),
                            ChannelManager.getFollowingChannels(),
                            BiFunction { channels: List<ChannelModel>, followingChannels: List<FollowingChannelModel> ->
                                Pair(channels, followingChannels)
                            }),
                    onNextNonContext = { (channels, followingChannels) ->
                        channelList = channels
                        followingfChannelList = followingChannels
                    },
                    onCompleteWithContext = onComplete

            )
    )

    fun updateChannelFollowers(id: Int, body: FollowersModel, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(ChannelManager.updateChannelFollowers(id, body),
                            ChannelManager.getFollowingChannels(),
                            BiFunction { channel: ChannelModel, followingChannels: List<FollowingChannelModel> ->
                                Pair(channel, followingChannels)
                            }
                    ),
                    onNextNonContext = { (channels, followingChannels) ->
                        channelModel = channels
                        followingfChannelList = followingChannels
                    },
                    onCompleteWithContext = onComplete

            )
    )
}