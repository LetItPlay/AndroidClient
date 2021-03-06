package com.letitplay.maugry.letitplay.data_management.repo.channel

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayDeleteApi
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPostApi
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayPutApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.*
import com.letitplay.maugry.letitplay.data_management.model.embeddedItemToTrackWithChannels
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.Result
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


class ChannelDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val postApi: LetItPlayPostApi,
        private val putApi: LetItPlayPutApi,
        private val deleteApi: LetItPlayDeleteApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : ChannelRepository {

    override fun followedChannelsId(): Flowable<List<Int>> {
        return db.channelDao().getFollowedChannelsId(preferenceHelper.contentLanguage!!)
                .subscribeOn(schedulerProvider.io())
    }

    override fun channel(channelId: Int): Flowable<ChannelWithFollow> {
        val localChannel = db.channelDao().getChannelWithFollow(channelId)
                .switchMap {
                    if (it.isEmpty()) {
                        api.getChannelPiece(channelId)
                                .doOnSuccess { db.channelDao().updateOrInsertChannel(listOf(it)) }
                                .map { ChannelWithFollow(it, null) }
                                .toFlowable()
                    } else {
                        Flowable.just(it.first())
                    }
                }
        return localChannel
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun channels(): Flowable<List<Channel>> {
        return api.channels()
                .doOnSuccess { db.channelDao().updateOrInsertChannel(it) }
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
                    when (currentIsFollowing) {
                        true -> deleteApi.unFollowChannel(channelId)
                                .map { it to { followDao.deleteFollowWithChannelId(channelData.id) } }
                        else -> putApi.updateChannelFollowers(channelId)
                                .map { it to { followDao.insertFollow(Follow(channelId)) } }

                    }
                }
                .doOnSuccess {
                    val channelModel = it.first
                    db.runInTransaction {
                        db.channelDao().updateOrInsertChannel(listOf(channelModel))
                        it.second()
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
                .doOnSuccess(db.channelDao()::updateOrInsertChannel)
                .subscribeOn(schedulerProvider.io())
                .toCompletable()
    }

    override fun recentAddedTracks(channelId: Int): Flowable<List<Track>> {
        return api.getChannelTracks(channelId)
                .map {
                    val channel = it.first().channel
                    val tracks = embeddedItemToTrackWithChannels(it).map(TrackWithChannel::track)
                    db.runInTransaction {
                        db.channelDao().updateOrInsertChannel(listOf(channel))
                        db.trackDao().insertTracks(tracks)
                    }
                }
                .map { Result.success(it) }
                .onErrorReturn { Result.failure(it.message, it) }
                .flatMapPublisher { db.trackDao().getChannelTracksByDate(channelId) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }
}