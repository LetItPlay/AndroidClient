package com.letitplay.maugry.letitplay.user_flow.business.feed

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4


object FeedPresenter : BasePresenter<IMvpView>() {

    var trackAndChannel: List<Pair<ChannelModel, TrackModel>>? = null
    var followingChannelList: List<FollowingChannelModel>? = null
    var favouriteTrackList: List<FavouriteTracksModel>? = null

    fun loadTracks(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            TrackManager.getTracks(),
                            ChannelManager.getFollowingChannels(),
                            ChannelManager.getChannels(),
                            TrackManager.getFavouriteTracks(),
                            Function4
                            { tracks: List<TrackModel>, followingChannels: List<FollowingChannelModel>, channel: List<ChannelModel>, favouriteTracks: List<FavouriteTracksModel> ->
                                ChannelWithTracks(tracks, followingChannels, channel, favouriteTracks)
                            }),
                    onNextNonContext = { channelWithTracks ->
                        trackAndChannel = channelWithTracks.tracks
                                .filter {
                                    val id = it.stationId
                                    channelWithTracks.followingChannels.find { it.id == id } != null
                                }
                                .sortedByDescending(TrackModel::publishedAt)
                                .map {
                                    val id = it.stationId
                                    val channel = channelWithTracks.channel.first { it.id == id }
                                    Pair(channel, it)
                                }
                        followingChannelList = channelWithTracks.followingChannels
                    },
                    onCompleteWithContext = onComplete
            )
    )

    class ChannelWithTracks(var tracks: List<TrackModel>, var followingChannels: List<FollowingChannelModel>,
                            var channel: List<ChannelModel>, var favouriteTracks: List<FavouriteTracksModel>)
}