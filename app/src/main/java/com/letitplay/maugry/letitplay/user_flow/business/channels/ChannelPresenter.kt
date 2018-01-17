package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
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

    var extendChannelList: List<ExtendChannelModel>? = null

    var updatedChannel: ChannelModel? = null

    fun loadChannels(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.getExtendChannel(),
                    triggerProgress = false,
                    onNextNonContext = {
                        extendChannelList = it
                    },
                    onCompleteWithContext = onComplete

            )
    )

    fun updateChannelFollowers(channel: ExtendChannelModel, body: FollowersModel, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.updateChannelFollowers(channel.id!!, body),
                    onNextNonContext = {
                        channel.following?.let{
                            it.isFollowing = !it.isFollowing
                            ChannelManager.updateFollowingChannels(it)
                        }
                        updatedChannel = it
                    },
                    onCompleteWithContext = onComplete
            )
    )
}