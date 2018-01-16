package com.letitplay.maugry.letitplay.user_flow.business.feed

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.Function4


object FeedPresenter : BasePresenter<IMvpView>() {

    var feedItemList: List<FeedItemModel>? = null

    var updatedTrack: TrackModel? = null

    fun loadTracks(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            TrackManager.getTracks(),
                            ChannelManager.getFollowingChannels(),
                            ChannelManager.getChannels(),
                            TrackManager.getFavouriteTracks(),
                            Function4
                            { tracks: List<TrackModel>, followingChannels: List<FollowingChannelModel>, channel: List<ChannelModel>, favouriteTrack: List<FavouriteTracksModel> ->
                                FeedVM(tracks, followingChannels, channel, favouriteTrack)
                            }),
                    onNextNonContext = { feedVM ->
                        feedItemList = feedVM.tracks
                                .filter {
                                    val id = it.stationId
                                    feedVM.followingChannels.find { it.id == id && it.isFollowing } != null
                                }
                                .sortedByDescending(TrackModel::publishedAt)
                                .map {
                                    val id = it.stationId
                                    val channel = feedVM.channel.first { it.id == id }
                                    Pair(channel, it)
                                }
                                .map {
                                    val id = it.second.id
                                    var like = feedVM.favouriteTrack.find { it.id == id }
                                    if (like == null) {
                                        like = FavouriteTracksModel(id, it.second.likeCount, false)
                                        like.save()
                                    }
                                    FeedItemModel(it.second, it.first, like)
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

    class FeedVM(var tracks: List<TrackModel>, var followingChannels: List<FollowingChannelModel>,
                            var channel: List<ChannelModel>,
                            var favouriteTrack: List<FavouriteTracksModel>)
}