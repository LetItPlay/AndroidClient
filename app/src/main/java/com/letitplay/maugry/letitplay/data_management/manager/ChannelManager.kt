package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.data_management.repo.*
import com.letitplay.maugry.letitplay.data_management.service.ServiceController


object ChannelManager : BaseManager() {

    fun getChannels() = get(
            local = { ChannelModel().queryAll() },
            remote = ServiceController.getChannels(),
            remoteWhen = { REMOTE_ALWAYS },
            update = { remote ->
                ChannelModel().deleteAll()
                remote.saveAll()
            }
    )

    fun updateChannelFollowers(id: Int, body: UpdateFollowersRequestBody) = ServiceController.updateChannelFollowers(id, body)

    fun getChannelPiece(id: Int) = get(
            local = { ChannelModel().query { equalTo("id", id) } }
    )

    fun updateExtendChannel(extendChannel: ExtendChannelModel) {
        extendChannel.save()
    }


    fun getFollowingChannels() = get(
            local = { FollowingChannelModel().queryAll() }
    )

    fun getFollowingChannelPiece(id: Int) = get(
            local = { FollowingChannelModel().query { equalTo("id", id) } }
    )

    fun updateFollowingChannels(followingChannelModel: FollowingChannelModel) {
        followingChannelModel.save()
    }
}