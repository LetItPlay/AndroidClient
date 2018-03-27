package com.letitplay.maugry.letitplay.data_management.repo.channel

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
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.Result
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


class ChannelDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val postApi: LetItPlayPostApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : ChannelRepository {

    override fun followedChannelsId(): Flowable<List<Int>> {
        return db.channelDao().getFollowedChannelsId(preferenceHelper.contentLanguage!!)
                .subscribeOn(schedulerProvider.io())
    }

    override fun channel(channelId: Int): Flowable<ChannelWithFollow> {
        return db.channelDao().getChannelWithFollow(channelId)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun channels(): Flowable<List<Channel>> {
        return api.channels()
                .doOnSuccess { db.channelDao().insertChannels(it) }
                .onErrorReturnItem(emptyList())
                .flatMapPublisher { db.channelDao().getAllChannels(preferenceHelper.contentLanguage!!) }
                .subscribeOn(schedulerProvider.io())
    }

    override fun channelFollowState(channelId: Int): Flowable<Boolean> {
        return db.followDao().getFollow(channelId)
                .map(List<Follow>::isNotEmpty)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun follow(channelData: Channel): Completable {
        val followDao = db.followDao()
        val channelId = channelData.id
        val isFollowing = Single.fromCallable { followDao.getFollowSync(channelId) != null }
        return isFollowing
                .flatMap {
                    val currentIsFollowing = it
                    val (request, handleFollowDb) = when {
                        currentIsFollowing -> UpdateFollowersRequestBody.UNFOLLOW to { followDao.deleteFollowWithChannelId(channelData.id) }
                        else -> UpdateFollowersRequestBody.FOLLOW to { followDao.insertFollow(Follow(channelId)) }
                    }
                    postApi.updateChannelFollowers(channelId, request)
                            .map { it to handleFollowDb }
                }
                .doOnSuccess {
                    val channelModel = toChannelModel(it.first)
                    db.runInTransaction {
                        it.second()
                        db.channelDao().updateChannel(channelModel)
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