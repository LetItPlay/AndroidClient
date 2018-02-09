package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.business.trends.TrendsPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import io.reactivex.Observable
import io.reactivex.functions.Function3

object ChannelPagePresenter : BasePresenter<IMvpView>() {

    var extendTrackList: List<TrackModel>? = null
    var extendChannel: ExtendChannelModel? = null
    var updatedChannel: ChannelModel? = null
    var playlist: List<AudioTrack>? = null
    var updatedTrack: TrackModel? = null

    val recentTracks: List<TrackModel>
        get() = extendTrackList?.sortedByDescending { it.publishedAt } ?: emptyList()

    fun loadTracks(id: Int, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.getChannelPiece(id),
                            ChannelManager.getFollowingChannelPiece(id),
                            TrackManager.getTrackPiece(id),
                            Function3
                            { channels: List<ChannelModel>, followingChannles: List<FollowingChannelModel>, tracks: List<TrackModel> ->
                                ChannelPageViewModel(channels.first(), followingChannles.firstOrNull(), tracks)
                            }),
                    triggerProgress = false,
                    onNextNonContext = { model ->
                        extendTrackList = model.tracks
                        extendChannel = ExtendChannelModel(model.channel.id, model.channel, model.followingChannel)
                        playlist = model.tracks.map {
                            (extendChannel?.channel to it).toAudioTrack()
                        }
                    },
                    onCompleteWithContext = onComplete

            )
    )

    fun updateListenersTracks(extendTrack: ExtendTrackModel, body: UpdateRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.updateFavouriteTrack(extendTrack.id?.toInt()!!, body),
                    triggerProgress = false,
                    onNextNonContext = {
                        updatedTrack = it
                        val listened = ListenedTrackModel(extendTrack.id, true)
                        TrackManager.updateListenedTrack(listened)
                        extendTrack.listened = listened
                        extendTrack.track?.listenCount = it.listenCount
                        TrackManager.updateExtendTrackModel(extendTrack)
                    },
                    onCompleteWithContext = onComplete
            )
    )

    fun updateChannelFollowers(channel: ExtendChannelModel, body: UpdateFollowersRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.updateChannelFollowers(channel.id!!, body),
                    triggerProgress = false,
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

    class ChannelPageViewModel(var channel: ChannelModel,
                               var followingChannel: FollowingChannelModel?,
                               var tracks: List<TrackModel>
    )
}