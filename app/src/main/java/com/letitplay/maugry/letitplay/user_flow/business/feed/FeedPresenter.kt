package com.letitplay.maugry.letitplay.user_flow.business.feed

import com.letitplay.maugry.letitplay.data_management.manager.FollowingChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPagePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object FeedPresenter : BasePresenter<IMvpView>() {
    fun loadTracks(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            TrackManager.getTracks(),
                            FollowingChannelManager.getFollowingChannels(),
                            BiFunction { channels: List<TrackModel>, followingChannels: List<FollowingChannelModel> ->
                                Pair(channels, followingChannels)
                            }),
                    onNextNonContext = { (channel, tracks) ->
                        ChannelPagePresenter.vmTrackList = tracks
                        ChannelPagePresenter.vmChannel = channel
                    },
                    onCompleteWithContext = onComplete

                    )
    )
}