package com.letitplay.maugry.letitplay.user_flow.business.feed

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object FeedPresenter : BasePresenter<IMvpView>() {

    var extendTrackList: List<ExtendTrackModel>? = null
    var playlist: List<AudioTrack>? = null
    var updatedTrack: TrackModel? = null

    fun loadTracks(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.getFollowingChannels(),
                            TrackManager.getExtendTrack(),
                            BiFunction { followingChannels: List<FollowingChannelModel>, tracks: List<ExtendTrackModel> ->
                                Pair(followingChannels, tracks)
                            }),
                    onNextNonContext = { (followingChannels, track) ->
                        extendTrackList = track.filter {
                            val idStation = it.track?.stationId
                            followingChannels.find { it.id == idStation && it.isFollowing} != null
                        }
                        playlist = extendTrackList?.map {
                            AudioTrack(
                                    id = it.track?.id!!,
                                    url = "$GL_MEDIA_SERVICE_URL${it.track?.audio?.fileUrl}",
                                    title = it.track?.name,
                                    subtitle = it.channel?.name,
                                    imageUrl = "$GL_MEDIA_SERVICE_URL${it.track?.image}",
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