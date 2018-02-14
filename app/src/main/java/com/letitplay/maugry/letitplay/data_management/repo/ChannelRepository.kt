package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import io.reactivex.Flowable


interface ChannelRepository {
    fun channels(): Flowable<List<Channel>>
    fun channel(channelId: Int): Flowable<Channel>
}