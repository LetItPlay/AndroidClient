package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.utils.Optional
import io.reactivex.Flowable


class DbChannelRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi
) : ChannelRepository {

    override fun channel(channelId: Int): Flowable<Channel> {
        return db.channelDao().getChannel(channelId)
    }

    override fun channels(): Flowable<List<Channel>> {
        return api.channels()
                .doOnSuccess { db.channelDao().insertChannels(it) }
                .map { Optional.of(it) }
                .onErrorReturnItem(Optional.none())
                .flatMapPublisher { db.channelDao().getAllChannels() }
    }
}