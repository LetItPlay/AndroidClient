package com.letitplay.maugry.letitplay.user_flow.business.trends

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

object TrendsPresenter : BasePresenter<IMvpView>() {

    var trackAndChannel: List<Pair<ChannelModel, TrackModel>>? = null
    var updatedTrack: TrackModel? = null

    fun loadTracks(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            TrackManager.getTracks(),
                            ChannelManager.getChannels(),
                            BiFunction { tracks: List<TrackModel>, channels: List<ChannelModel> ->
                                Pair(tracks, channels)
                            }),
                    onNextNonContext = { (tracks, channels) ->
                        trackAndChannel = tracks
                                .sortedByDescending { it.likeCount }
                                .map {
                                    val id = it.stationId
                                    val channel = channels.first { it.id == id }
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
}