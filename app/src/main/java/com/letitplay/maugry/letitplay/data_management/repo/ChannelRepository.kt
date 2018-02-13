package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.api.Service
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import io.reactivex.Flowable


interface ChannelRepository {
    fun channels(): Flowable<List<Channel>>
}

class ChannelRepositoryImpl(
        private val api: Service
): ChannelRepository {
    override fun channels(): Flowable<List<Channel>> {
        return api.channels().toFlowable()
    }
}