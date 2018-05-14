package com.letitplay.maugry.letitplay.data_management.repo.channel

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.*
import com.letitplay.maugry.letitplay.data_management.model.embeddedItemToTrackWithChannels
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.Result
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction


class ChannelDataRepository(
        private val db: LetItPlayDb,
        private val api: LetItPlayApi,
        private val schedulerProvider: SchedulerProvider,
        private val preferenceHelper: PreferenceHelper
) : ChannelRepository {

    override fun followedChannelsId(): Flowable<List<Int>> {
        return db.channelDao().getFollowedChannelsId(preferenceHelper.contentLanguage!!)
                .subscribeOn(schedulerProvider.io())
    }

    override fun channel(channelId: Int): Flowable<Channel> {
        return api.getChannelPiece(channelId)
                .toFlowable()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun channels(categoryId: Int?): Single<List<Channel>> {
        val source = when (categoryId) {
            null -> api.channels()
            -1 -> api.favouriteChannels()
            else -> api.channelsFrmoCategory(categoryId)
        }
        return source
                .onErrorReturnItem(emptyList())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun catalog(): Flowable<Pair<List<Channel>, List<Category>>> {
        return Single.zip(api.favouriteChannels(), api.catalog(),
                BiFunction { channels: List<Channel>, catalog: List<Category> ->
                    Pair(channels, catalog)
                })
                .toFlowable()
                .onErrorReturnItem(Pair(emptyList(), emptyList()))
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }


    override fun loadChannels(): Completable {
        return api.channels()
                .doOnSuccess(db.channelDao()::updateOrInsertChannel)
                .subscribeOn(schedulerProvider.io())
                .toCompletable()
    }

    override fun channelFollowState(channelId: Int): Flowable<Boolean> {
        return db.followDao().getFollow(channelId)
                .map(List<Follow>::isNotEmpty)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun follow(channelData: Channel): Single<Channel> {
        val isFollowing = Single.fromCallable { channelData.followed != null }
        return isFollowing
                .flatMap {
                    val currentIsFollowing = it
                    when (currentIsFollowing) {
                        true -> api.unFollowChannel(channelData.id)
                        else -> api.updateChannelFollowers(channelData.id)
                    }
                }
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
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