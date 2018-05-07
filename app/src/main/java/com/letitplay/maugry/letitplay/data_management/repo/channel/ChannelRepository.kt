package com.letitplay.maugry.letitplay.data_management.repo.channel

import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


interface ChannelRepository {
    fun channels(): Flowable<List<Channel>>
    fun channelsWithFollow(): Flowable<List<ChannelWithFollow>>
    fun catalog(): Flowable<List<Category>>
    fun channel(channelId: Int): Flowable<ChannelWithFollow>
    fun follow(channel: Channel): Completable
    fun loadChannels(): Completable
    fun recentAddedTracks(channelId: Int): Flowable<List<Track>>
    fun followedChannelsId(): Flowable<List<Int>>
    fun channelFollowState(trackId: Int): Flowable<Boolean>
}