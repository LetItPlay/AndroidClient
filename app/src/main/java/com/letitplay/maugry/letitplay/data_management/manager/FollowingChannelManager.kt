package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.repo.queryAll


object FollowingChannelManager : BaseManager() {

    fun getFollowingChannels() = get(
            local = { FollowingChannelModel().queryAll() }
    )
}