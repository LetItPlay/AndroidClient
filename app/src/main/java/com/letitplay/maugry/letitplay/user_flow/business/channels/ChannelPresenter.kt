package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelItemModel
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object ChannelPresenter : BasePresenter<IMvpView>() {

    var channelItemsList: List<ChannelItemModel>? = null

    var updatedChannel: ChannelModel? = null

    fun loadChannels(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.getChannels(),
                            ChannelManager.getFollowingChannels(),
                            BiFunction { channel: List<ChannelModel>, followingChannel: List<FollowingChannelModel> ->
                                Pair(channel, followingChannel)
                            }
                    ),
                    onNextNonContext = { (channel, folowingChannel) ->
                        channelItemsList = channel.map {
                            val id = it.id
                            var followingChannel = folowingChannel.find { it.id == id }
                            if (followingChannel == null) {
                                followingChannel = FollowingChannelModel(id, false)
                                followingChannel.save()
                            }
                            ChannelItemModel(it, followingChannel)
                        }
                    },
                    onCompleteWithContext = onComplete

            )
    )

    fun updateChannelFollowers(id: Int, body: FollowersModel, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.updateChannelFollowers(id, body),
                    onNextNonContext = {
                        updatedChannel = it
                    },
                    onCompleteWithContext = onComplete
            )
    )
}