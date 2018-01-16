package com.letitplay.maugry.letitplay.user_flow.business.trends

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.Function3

object TrendsPresenter : BasePresenter<IMvpView>() {

    var feedItemList: List<FeedItemModel>? = null
    var updatedTrack: TrackModel? = null

    fun loadTracks(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            TrackManager.getTracks(),
                            ChannelManager.getChannels(),
                            TrackManager.getFavouriteTracks(),
                            Function3
                            { tracks: List<TrackModel>, channels: List<ChannelModel>, favouriteTracks: List<FavouriteTracksModel> ->
                                TrendsVM(tracks, channels, favouriteTracks)
                            }),
                    onNextNonContext = { trendsVM ->
                        feedItemList = trendsVM.tracks
                                .sortedByDescending { it.likeCount }
                                .map {
                                    val id = it.stationId
                                    val channel = trendsVM.channel.first { it.id == id }
                                    Pair(channel, it)
                                }
                                .map {
                                    val id = it.second.id
                                    var like = trendsVM.favouriteTrack.find { it.id == id }
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

    class TrendsVM(var tracks: List<TrackModel>,
                   var channel: List<ChannelModel>,
                   var favouriteTrack: List<FavouriteTracksModel>)
}