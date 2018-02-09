package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.queryAll
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.data_management.repo.saveAll
import com.letitplay.maugry.letitplay.data_management.service.ServiceController
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object ChannelManager : BaseManager() {

    fun getChannels() = get(
            local = { ChannelModel().queryAll() },
            remote = ServiceController.getChannels(),
            remoteWhen = { REMOTE_ALWAYS },
            update = { remote ->
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

    fun getExtendChannel() = get(
            local = { ExtendChannelModel().queryAll() }
    )

    fun queryChannels(query: String, contentLang: String): Observable<List<ExtendChannelModel>> = Observable.zip(
            getChannels(),
            getFollowingChannels(),
            BiFunction { channels: List<ChannelModel>, followingChannels: List<FollowingChannelModel> ->
                channels.filter {
                    it.name!!.contains(query, true) || it.tags?.any { it.contains(query, true) } ?: false
                }
                        .filter { it.lang == contentLang }
                        .map {
                            val stationId = it.id
                            ExtendChannelModel(it.id, it, followingChannels.find { it.id == stationId })
                        }
            }
    )


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