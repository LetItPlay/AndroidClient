package com.letitplay.maugry.letitplay.user_flow.business.feed

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.FeedManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import io.reactivex.Observable
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers


object FeedPresenter : BasePresenter<IMvpView>() {

    var extendTrackList: List<ExtendTrackModel>? = null
    var playlist: List<AudioTrack>? = null
    var updatedTrack: TrackModel? = null
    var followingCount: String = ""

    fun loadTracks(triggerProgress: Boolean = true,
                   onError: ((IMvpView?, Throwable) -> Unit)? = null,
                   onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    triggerProgress = triggerProgress,
                    asyncObservable = ChannelManager.getFollowingChannels().doOnNext { model ->
                        followingCount = model.map { it.id }.joinToString(",")
                    }.concatMap {
                        Observable.zip(
                                FeedManager.getFeed(followingCount, 1000, currentContentLang?.name?.toLowerCase() ?: "ru"),
                                TrackManager.getFavouriteTracks(),
                                ChannelManager.getFollowingChannels(),
                                TrackManager.getListenedTracks(),
                                Function4
                                { feed: FeedModel, favouriteTracks: List<FavouriteTracksModel>, followingChannels: List<FollowingChannelModel>, listenedTracks: List<ListenedTrackModel> ->
                                    FeedViewModel(feed, favouriteTracks, followingChannels, listenedTracks)
                                })
                                .observeOn(Schedulers.io())
                                .doOnNext { model ->
                                    extendTrackList = model.feed.tracks?.map {
                                        val stationId = it.stationId
                                        val trackId = it.id
                                        ExtendTrackModel(trackId, it, model.feed.channels?.find { it.id == stationId },
                                                model.favouriteTrack.find { it.id == trackId },
                                                model.listenedTracks.find { it.id == trackId })
                                    }
                                }
                    },
                    onNextNonContext = {
                        playlist = extendTrackList?.map {
                            (it.channel to it.track).toAudioTrack()
                        }
                    },
                    onErrorWithContext = onError,
                    onCompleteWithContext = onComplete
            )
    )

    fun updateListenersTracks(extendTrack: ExtendTrackModel, body: UpdateRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.updateFavouriteTrack(extendTrack.id?.toInt()!!, body),
                    triggerProgress = false,
                    onNextNonContext = {
                        updatedTrack = it
                        extendTrack.track?.listenCount = it.listenCount
                        TrackManager.updateExtendTrackModel(extendTrack)
                        extendTrack.listened?.let {
                            it.isListened = true
                            TrackManager.updateListenedTrack(it)
                        }

                    },
                    onCompleteWithContext = onComplete
            )
    )

    fun updateFavouriteTracks(extendTrack: ExtendTrackModel, body: UpdateRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.updateFavouriteTrack(extendTrack.id?.toInt()!!, body),
                    triggerProgress = false,
                    onNextNonContext = {
                        updatedTrack = it
                        if (extendTrack.like == null) {
                            val like = FavouriteTracksModel(extendTrack.id, true)
                            TrackManager.updateFavouriteTrack(like)
                            extendTrack.like = like
                        } else {
                            extendTrack.like?.let {
                                it.isLiked = !it.isLiked
                                TrackManager.updateFavouriteTrack(it)
                            }
                        }
                        extendTrack.track?.likeCount = it.likeCount
                        TrackManager.updateExtendTrackModel(extendTrack)
                    },
                    onCompleteWithContext = onComplete
            )

    )

    class FeedViewModel(var feed: FeedModel,
                        var favouriteTrack: List<FavouriteTracksModel>,
                        var followingChannels: List<FollowingChannelModel>,
                        var listenedTracks: List<ListenedTrackModel>
    )

}