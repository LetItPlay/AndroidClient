package com.letitplay.maugry.letitplay.user_flow.business.trends

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.FeedManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import io.reactivex.Observable
import io.reactivex.functions.Function5
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.Days

object TrendsPresenter : BasePresenter<IMvpView>() {

    var extendTrackList: List<ExtendTrackModel>? = null
    var extendChannelList: List<ChannelModel>? = null
    var playlist: List<AudioTrack>? = null
    var updatedTrack: TrackModel? = null

    fun loadTracksAndChannels(
            triggerProgress: Boolean = true,
            onError: ((IMvpView?, Throwable) -> Unit)? = null,
            onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    triggerProgress = triggerProgress,
                    asyncObservable = Observable.zip(
                            FeedManager.getTrends(FeedPresenter.currentContentLang?.name?.toLowerCase() ?: "ru"),
                            ChannelManager.getChannels(),
                            TrackManager.getFavouriteTracks(),
                            ChannelManager.getFollowingChannels(),
                            TrackManager.getListenedTracks(),
                            Function5
                            { feed: FeedModel, channels: List<ChannelModel>,
                              favouriteTracks: List<FavouriteTracksModel>, followingChannels: List<FollowingChannelModel>, listenedTracks: List<ListenedTrackModel> ->
                                TrendsViewModel(feed, channels, favouriteTracks, followingChannels, listenedTracks)
                            })
                            .observeOn(Schedulers.io())
                            .doOnNext { model ->
                                extendTrackList = model.feed.tracks?.map {
                                    val stationId = it.stationId
                                    val trackId = it.id
                                    ExtendTrackModel(it.id, it, model.feed.channels?.find { it.id == stationId },
                                            model.favouriteTrack.find { it.id == trackId },
                                            model.listenedTracks.find { it.id == trackId })
                                }

                                extendChannelList = model.channels.filter { it.lang?.toUpperCase() == currentContentLang?.name }
                            },
                    onErrorWithContext = onError,
                    onNextNonContext = {
                        playlist = extendTrackList?.map {
                            (it.channel to it.track).toAudioTrack()
                        }
                    },
                    onCompleteWithContext = onComplete
            )
    )

    fun updateFavouriteTracks(id: Int, extendTrack: ExtendTrackModel, body: UpdateRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.updateFavouriteTrack(id, body),
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

    private fun List<ExtendTrackModel>.takeLastDate(now: DateTime): List<ExtendTrackModel> =
            sortedByDescending {
                it.track?.publishedAt
            }.filter {
                it.track?.publishedAt != null
            }.takeWhile {
                val publish = DateTime(it.track?.publishedAt?.time!!)
                val days = Days.daysBetween(publish, now).days
                days in 0..7
            }


    private fun List<ExtendTrackModel>.sortByListenCount(): List<ExtendTrackModel> =
            sortedByDescending {
                it.track?.listenCount
            }

    class TrendsViewModel(var feed: FeedModel,
                          var channels: List<ChannelModel>,
                          var favouriteTrack: List<FavouriteTracksModel>,
                          var followingChannels: List<FollowingChannelModel>,
                          var listenedTracks: List<ListenedTrackModel>
    )

}