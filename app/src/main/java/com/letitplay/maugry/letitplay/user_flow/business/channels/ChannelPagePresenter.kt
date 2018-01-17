package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object ChannelPagePresenter : BasePresenter<IMvpView>() {

    var extendTrackList: List<ExtendTrackModel>? = null
    var extendChannel: ExtendChannelModel? = null
    var updatedChannel: ChannelModel? = null


    fun loadTracks(id: Int, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.getExtendChannelPiece(id),
                            TrackManager.getPieceExtendTrack(id),
                            BiFunction { channels: List<ExtendChannelModel>, tracks: List<ExtendTrackModel> ->
                                Pair(channels.firstOrNull(), tracks)
                            }),
                    onNextNonContext = { (channel, tracks) ->
                        extendTrackList = tracks
                        extendChannel = channel
                    },
                    onCompleteWithContext = onComplete

            )
    )

    fun updateChannelFollowers(channel: ExtendChannelModel, body: FollowersModel, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.updateChannelFollowers(channel.id!!, body),
                    onNextNonContext = {
                        channel.following?.let {
                            it.isFollowing = !it.isFollowing
                            ChannelManager.updateFollowingChannels(it)
                        }
                        updatedChannel = it
                    },
                    onCompleteWithContext = onComplete
            )
    )
}