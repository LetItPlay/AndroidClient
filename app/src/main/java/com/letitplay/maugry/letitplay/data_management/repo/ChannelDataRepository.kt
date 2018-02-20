package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.api.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.Follow
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.model.toChannelModel
import com.letitplay.maugry.letitplay.utils.Optional
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.Result
import io.reactivex.Completable
import io.reactivex.Flowable


class ChannelDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val postApi: LetItPlayPostApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : ChannelRepository {
    override fun channel(channelId: Int): Flowable<ChannelWithFollow> {
        return db.channelDao().getChannelWithFollow(channelId)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun channels(): Flowable<List<Channel>> {
        return api.channels()
                .doOnSuccess { db.channelDao().insertChannels(it) }
                .map { Optional.of(it) }
                .onErrorReturnItem(Optional.none())
                .flatMapPublisher { db.channelDao().getAllChannels(preferenceHelper.contentLanguage!!) }
                .subscribeOn(schedulerProvider.io())
    }

    override fun follow(channelData: ChannelWithFollow): Completable {
        val followDao = db.followDao()
        val channel = channelData.channel
        val (request, handleFollowDb) = when {
            channelData.isFollowing -> UpdateFollowersRequestBody.UNFOLLOW to { followDao.deleteFollowWithChannelId(channelData.followId!!) }
            else -> UpdateFollowersRequestBody.FOLLOW to { followDao.insertFollow(Follow(channel.id)) }
        }
        return postApi.updateChannelFollowers(channel.id, request)
                .map { Optional.of(toChannelModel(it)) }
                .onErrorReturnItem(Optional.none())
                .doOnSuccess {
                    if (it.value != null) {
                        db.runInTransaction {
                            handleFollowDb()
                            db.channelDao().updateChannel(it.value)
                        }
                    }
                }
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
                .toCompletable()
    }

    override fun channelsWithFollow(): Flowable<List<ChannelWithFollow>> {
        return db.channelDao()
                .getAllChannelsWithFollow(preferenceHelper.contentLanguage!!)
                .subscribeOn(schedulerProvider.io())
    }

    override fun loadChannels(): Completable {
        return api.channels()
                .doOnSuccess {
                    db.channelDao().insertChannels(it)
                }
                .subscribeOn(schedulerProvider.io())
                .toCompletable()
    }

    override fun recentAddedTracks(channelId: Int): Flowable<List<Track>> {
        return api.getChannelTracks(channelId)
                .doOnSuccess { db.trackDao().insertTracks(it) }
                .map { Result.success(it) }
                .onErrorReturn { Result.failure(it.message, it) }
                .flatMapPublisher { db.trackDao().getChannelTracksByDate(channelId) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }
}