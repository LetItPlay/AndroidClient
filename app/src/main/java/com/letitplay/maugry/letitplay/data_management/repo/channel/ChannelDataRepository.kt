package com.letitplay.maugry.letitplay.data_management.repo.channel

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.LetItPlayDb
import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.embeddedItemToTrackWithChannels
import com.letitplay.maugry.letitplay.user_flow.ui.ChannelListType
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.Result
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

    override fun channels(listType: Int): Single<List<Channel>> {
        val source = when (listType) {
            ChannelListType.NORMAL -> api.channels()
            ChannelListType.BLACKLIST -> api.channelsFromBlackList()
            ChannelListType.FAVOURITE -> api.favouriteChannels()
            else -> api.channelsFromCategory(listType)
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


    override fun hideChannel(channelId: Int): Single<Channel> {
        return api.putChannelToBlacklist(channelId)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

    override fun follow(channel: Channel): Single<Channel> {
        val isFollowing = Single.fromCallable { channel.followed != null }
        return isFollowing
                .flatMap {
                    val currentIsFollowing = it
                    when (currentIsFollowing) {
                        true -> api.unFollowChannel(channel.id)
                        else -> api.updateChannelFollowers(channel.id)
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