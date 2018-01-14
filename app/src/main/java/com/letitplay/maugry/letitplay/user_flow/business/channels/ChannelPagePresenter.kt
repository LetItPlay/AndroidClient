package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object ChannelPagePresenter : BasePresenter<IMvpView>() {

    var vmTrackList: List<TrackModel>? = null
    var vmChannel: ChannelModel? = null
    var updatedChannel: ChannelModel? = null


    fun loadTracks(id: Int, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.getChannelPiece(id),
                            TrackManager.getPieceTracks(id),
                            BiFunction { channels: List<ChannelModel>, tracks: List<TrackModel> ->
                                Pair(channels.firstOrNull(), tracks)
                            }),
                    onNextNonContext = { (channel, tracks) ->
                        vmTrackList = tracks
                        vmChannel = channel
                    },
                    onCompleteWithContext = onComplete

            )
    )

    fun updateChannelFollowers(id: Int, body: FollowersModel, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.updateChannelFollowers(id, body),
                    onNextNonContext = {
                        updatedChannel = it
                    },
                    onCompleteWithContext = onComplete
            )
    )
}