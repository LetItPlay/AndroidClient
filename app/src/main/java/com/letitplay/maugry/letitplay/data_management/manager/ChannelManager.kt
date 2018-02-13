package com.letitplay.maugry.letitplay.data_management.manager


object ChannelManager : BaseManager() {

//    fun getChannels() = get(
//            local = { Channel().queryAll() },
//            remote = ServiceController.getChannels(),
//            remoteWhen = { REMOTE_ALWAYS },
//            update = { remote ->
//                Channel().deleteAll()
//                remote.saveAll()
//            }
//    )
//
//    fun updateChannelFollowers(id: Int, body: UpdateFollowersRequestBody) = ServiceController.updateChannelFollowers(id, body)
//
//    fun getChannelPiece(id: Int) = get(
//            local = { Channel().query { equalTo("id", id) } }
//    )
//
//    fun updateExtendChannel(extendChannel: ExtendChannelModel) {
//        extendChannel.save()
//    }
//
//
//    fun getFollowingChannels() = get(
//            local = { FollowingChannelModel().queryAll() }
//    )
//
//    fun getFollowingChannelPiece(id: Int) = get(
//            local = { FollowingChannelModel().query { equalTo("id", id) } }
//    )
//
//    fun updateFollowingChannels(followingChannelModel: FollowingChannelModel) {
//        followingChannelModel.save()
//    }
}