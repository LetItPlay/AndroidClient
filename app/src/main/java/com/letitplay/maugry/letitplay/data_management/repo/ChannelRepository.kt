package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import io.reactivex.Flowable
import io.reactivex.Single


interface ChannelRepository {
    fun channels(): Flowable<List<Channel>>
    fun channel(channelId: Int): Flowable<ChannelWithFollow>
    fun follow(channelData: ChannelWithFollow): Single<Boolean>
}