package com.letitplay.maugry.letitplay.user_flow.business.feed

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.Function3


object FeedPresenter : BasePresenter<IMvpView>() {

    var trackAndChannel: List<Pair<ChannelModel, TrackModel>>? = null

    var updatedTrack: TrackModel? = null

    fun loadTracks(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            TrackManager.getTracks(),
                            ChannelManager.getFollowingChannels(),
                            ChannelManager.getChannels(),
                            Function3
                            { tracks: List<TrackModel>, followingChannels: List<FollowingChannelModel>, channel: List<ChannelModel> ->
                                ChannelWithTracks(tracks, followingChannels, channel)
                            }),
                    onNextNonContext = { channelWithTracks ->
                        trackAndChannel = channelWithTracks.tracks
                                .filter {
                                    val id = it.stationId
                                    channelWithTracks.followingChannels.find { it.id == id && it.isFollowing} != null
                                }
                                .sortedByDescending(TrackModel::publishedAt)
                                .map {
                                    val id = it.stationId
                                    val channel = channelWithTracks.channel.first { it.id == id }
                                    Pair(channel, it)
                                }
                    },
                    onCompleteWithContext = onComplete
            )
    )


    fun updateFavouriteTracks(id: Int, body: LikeModel, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.updateFavouriteTrack(id, body),
                    onNextNonContext = {
                        updatedTrack = it
                    },
                    onCompleteWithContext = onComplete
            )

    )

    class ChannelWithTracks(var tracks: List<TrackModel>, var followingChannels: List<FollowingChannelModel>,
                            var channel: List<ChannelModel>)
}