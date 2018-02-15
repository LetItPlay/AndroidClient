package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.Follow
import com.letitplay.maugry.letitplay.data_management.model.toChannelModel
import com.letitplay.maugry.letitplay.utils.Optional
import io.reactivex.Flowable
import io.reactivex.Single


class DbChannelRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val postApi: LetItPlayPostApi
) : ChannelRepository {

    override fun channel(channelId: Int): Flowable<ChannelWithFollow> {
        return db.channelDao().getChannelWithFollow(channelId)
    }

    override fun channels(): Flowable<List<Channel>> {
        return api.channels()
                .doOnSuccess { db.channelDao().insertChannels(it) }
                .map { Optional.of(it) }
                .onErrorReturnItem(Optional.none())
                .flatMapPublisher { db.channelDao().getAllChannels() }
    }

    override fun follow(channelData: ChannelWithFollow): Single<Boolean> {
        val followDao = db.followDao()
        val channel = channelData.channel
        val (request, handleFollowDb) = when {
            channelData.isFollowing -> UpdateFollowersRequestBody.UNFOLLOW to { followDao.deleteFollowWithChannelId(channelData.followId!!) }
            else -> UpdateFollowersRequestBody.FOLLOW to { followDao.insertFollow(Follow(channel.id)) }
        }
        return postApi.updateChannelFollowers(channel.id, request)
                .map { Optional.of(toChannelModel(it)) }
                .onErrorReturnItem(Optional.none())
                .map {
                    if (it.value != null) {
                        handleFollowDb()
                        db.channelDao().updateChannel(it.value)
                    }
                    it.value != null
                }
    }

    override fun channelsWithFollow(): Flowable<List<ChannelWithFollow>> {
        return api.channels()
                .doOnSuccess { db.channelDao().insertChannels(it) }
                .map { Optional.of(it) }
                .onErrorReturnItem(Optional.none())
                .flatMapPublisher { db.channelDao().getAllChannelsWithFollow() }
    }
}