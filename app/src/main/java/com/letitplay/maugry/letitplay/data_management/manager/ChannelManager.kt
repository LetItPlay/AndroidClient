package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.ChannelItemModel
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.repo.*
import com.letitplay.maugry.letitplay.data_management.service.ServiceController
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object ChannelManager : BaseManager() {

    fun getChannels() = get(
            local = { ChannelModel().queryAll() },
            remote = ServiceController.getChannels(),
            remoteWhen = { local -> local.isEmpty() },
            update = { remote ->
                ChannelModel().deleteAll()
                remote.saveAll()
            }
    )

    fun updateChannelFollowers(id: Int, body: FollowersModel) = ServiceController.updateChannelFollowers(id, body)

    fun updateExtendChannel(extendChannelList: List<ExtendChannelModel>) {
        ExtendChannelModel().deleteAll()
        extendChannelList.forEach {
            if (it.following == null) {
                val followingChannel = FollowingChannelModel(it.id, false)
                updateFollowingChannels(followingChannel)
                it.following = followingChannel
            }
        }
        extendChannelList.saveAll()
    }

    fun updateFollowingChannels(followingChannel: FollowingChannelModel) {
        followingChannel.save()

    }

    fun getExtendChannel() = get(
            local = { ExtendChannelModel().queryAll() }
    )

    fun getExtendChannelPiece(id: Int) = get(
            local = { ExtendChannelModel().query { it.equalTo("id", id) } }
    )

    fun queryChannels(query: String): Observable<List<ChannelItemModel>> =
            Observable.zip(
                    getChannels().map { channels ->
                        channels.filter { channel ->
                            channel.name!!.contains(query) || channel.tags?.contains(query) ?: false
                        }
                    },
                    getFollowingChannels(),
                    BiFunction { foundedChannels, followingChannels ->
                        foundedChannels.map { channel ->
                            var followingChannel = followingChannels.find { channel.id == it.id }
                            if (followingChannel == null) {
                                followingChannel = FollowingChannelModel(channel.id, false)
                                followingChannel.save()
                            }
                            ChannelItemModel(channel, followingChannel)
                        }
                    })

    fun getFollowingChannels() = get(
            local = { FollowingChannelModel().queryAll() }
    )
}