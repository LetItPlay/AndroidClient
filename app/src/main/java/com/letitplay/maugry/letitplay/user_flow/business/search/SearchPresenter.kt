package com.letitplay.maugry.letitplay.user_flow.business.search

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object SearchPresenter : BasePresenter<IMvpView>() {
    var queryResult: Pair<List<ExtendChannelModel>, List<AudioTrack>> = Pair(emptyList(), emptyList())
    var lastQuery: String? = null
    var updatedChannel: ChannelModel? = null

    fun executeQuery(query: String, onComplete: ((IMvpView?) -> Unit)) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.queryChannels(query, currentContentLang?.name?.toLowerCase() ?: "ru"),
                            TrackManager.queryTracks(query, currentContentLang?.name?.toLowerCase() ?: "ru"),
                            BiFunction
                            { foundedChannels: List<ExtendChannelModel>, foundedTracks: List<AudioTrack> ->

                                foundedChannels to foundedTracks
                            }),
                    onNextNonContext = { queryResult = it },
                    onCompleteWithContext = onComplete
            )
    )

    fun updateChannelFollowers(channel: ExtendChannelModel, body: UpdateFollowersRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.updateChannelFollowers(channel.id!!, body),
                    onNextNonContext = {
                        updatedChannel = it
                        if (channel.following == null) {
                            val following = FollowingChannelModel(channel.id, true)
                            ChannelManager.updateFollowingChannels(following)
                            channel.following = following
                        } else {
                            channel.following?.let {
                                it.isFollowing = !it.isFollowing
                                ChannelManager.updateFollowingChannels(it)
                            }
                        }
                        channel.channel?.subscriptionCount = it.subscriptionCount
                        ChannelManager.updateExtendChannel(channel)
                    },
                    onCompleteWithContext = onComplete
            )
    )
}