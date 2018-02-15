package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


interface ChannelRepository {
    fun channels(): Flowable<List<Channel>>
    fun channelsWithFollow(): Flowable<List<ChannelWithFollow>>
    fun channel(channelId: Int): Flowable<ChannelWithFollow>
    fun follow(channelData: ChannelWithFollow): Single<Boolean>
    fun loadChannels(): Completable
}