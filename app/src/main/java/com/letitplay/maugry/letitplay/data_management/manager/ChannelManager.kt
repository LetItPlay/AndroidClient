package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.repo.deleteAll
import com.letitplay.maugry.letitplay.data_management.repo.queryAll
import com.letitplay.maugry.letitplay.data_management.repo.saveAll
import com.letitplay.maugry.letitplay.data_management.service.ServiceController


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
}