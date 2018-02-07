package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.data_management.repo.*
import com.letitplay.maugry.letitplay.data_management.service.ServiceController
import io.reactivex.Observable


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

    fun updateExtendChannel(extendChannelList: List<ExtendChannelModel>) {
        ExtendChannelModel().deleteAll()
        val followingChannelModels = mutableListOf<FollowingChannelModel>()
        extendChannelList.forEach {
            if (it.following == null) {
                val followingChannel = FollowingChannelModel(it.id, false)
                followingChannelModels.add(followingChannel)
                it.following = followingChannel
            }
        }
        followingChannelModels.saveAll()
        extendChannelList.saveAll()
    }

    fun updateExtendChannel(extendChannel: ExtendChannelModel) {
        extendChannel.save()
    }

    fun getExtendChannel() = get(
            local = { ExtendChannelModel().queryAll() }
    )

    fun getExtendChannelPiece(id: Int) = get(
            local = { ExtendChannelModel().query { equalTo("id", id) } }
    )

    fun queryChannels(query: String): Observable<List<ExtendChannelModel>> = getExtendChannel().map { channels ->
        channels.filter {
            val channel = it.channel!!
            channel.name!!.contains(query, true) || channel.tags?.any { it.contains(query, true) } ?: false
        }
    }


    fun getFollowingChannels() = get(
            local = { FollowingChannelModel().queryAll() }
    )

    fun updateFollowingChannels(followingChannelModel: FollowingChannelModel) {
        followingChannelModel.save()
    }
}