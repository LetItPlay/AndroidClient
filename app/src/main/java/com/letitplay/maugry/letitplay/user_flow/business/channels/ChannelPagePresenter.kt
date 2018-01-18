package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object ChannelPagePresenter : BasePresenter<IMvpView>() {

    var extendTrackList: List<ExtendTrackModel>? = null
    var extendChannel: ExtendChannelModel? = null
    var updatedChannel: ChannelModel? = null
    var playlist: List<AudioTrack>? = null

    fun loadTracks(id: Int, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.getExtendChannelPiece(id),
                            TrackManager.getPieceExtendTrack(id),
                            BiFunction { channels: List<ExtendChannelModel>, tracks: List<ExtendTrackModel> ->
                                Pair(channels.firstOrNull(), tracks)
                            }),
                    triggerProgress = false,
                    onNextNonContext = { (channel, tracks) ->
                        extendTrackList = tracks
                        extendChannel = channel
                        playlist = tracks.map {
                            AudioTrack(
                                    id = it.track?.id!!,
                                    url = "${GL_MEDIA_SERVICE_URL}${it.track?.audio?.fileUrl}",
                                    title = it.track?.name,
                                    subtitle = it.channel?.name,
                                    imageUrl = "${GL_MEDIA_SERVICE_URL}${it.track?.image}",
                                    channelTitle = it.channel?.name,
                                    length = it.track?.audio?.lengthInSeconds,
                                    listenCount = it.track?.listenCount,
                                    publishedAt = it.track?.publishedAt
                            )
                        }
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