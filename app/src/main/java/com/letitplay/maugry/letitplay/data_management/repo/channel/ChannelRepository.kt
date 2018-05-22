package com.letitplay.maugry.letitplay.data_management.repo.channel

import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


interface ChannelRepository {
    fun channels(categoryId: Int): Single<List<Channel>>
    fun catalog(): Flowable<Pair<List<Channel>, List<Category>>>
    fun channel(channelId: Int): Flowable<Channel>
    fun follow(channel: Channel): Single<Channel>
    fun hideChannel(channelId: Int): Single<Channel>
    fun recentAddedTracks(channelId: Int): Flowable<List<Track>>
    fun followedChannelsId(): Flowable<List<Int>>
}