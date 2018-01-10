package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.data_management.repo.deleteAll
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.queryAll
import com.letitplay.maugry.letitplay.data_management.repo.saveAll
import com.letitplay.maugry.letitplay.data_management.service.ServiceController
import io.reactivex.Observable


object ChannelManager : BaseManager() {

    fun getChannels() = get(
            local = { ChannelModel().queryAll() },
            remote = ServiceController.getChannels(),
            remoteWhen = { true },
            update = { remote ->
                ChannelModel().deleteAll()
                remote.saveAll()
            }
    )

    fun updateChannelFollowers(id: Int, body: FollowersModel) = ServiceController.updateChannelFollowers(id, body)

    fun getChannelPiece(id: Int) = get(
            local = { ChannelModel().query { it.equalTo("id", id) } }
    )

    fun queryChannels(query: String): Observable<List<ChannelModel>> = getChannels().map { channels ->
        channels.filter { channel ->
            channel.name!!.contains(query) || channel.tags?.contains(query) ?: false
        }
    }
}