package com.letitplay.maugry.letitplay.user_flow.business.feed

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.business.Splash.SplashPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object FeedPresenter : BasePresenter<IMvpView>() {

    var extendTrackList: List<ExtendTrackModel>? = null
    var playlist: List<AudioTrack>? = null
    var updatedTrack: TrackModel? = null

    fun loadTracks(triggerProgress: Boolean = true,
                   onError: ((IMvpView?, Throwable) -> Unit)? = null,
                   onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    triggerProgress = triggerProgress,
                    asyncObservable = Observable.zip(
                            ChannelManager.getFollowingChannels(),
                            TrackManager.getExtendTrack(),
                            BiFunction { followingChannels: List<FollowingChannelModel>, tracks: List<ExtendTrackModel> ->
                                val newTracks = tracks.filter {
                                    val idStation = it.track?.stationId
                                    val lang = it.track?.lang?.let { lang -> ContentLanguage.getLanguage(lang) }
                                    followingChannels.find { it.id == idStation && it.isFollowing } != null && currentContentLang == lang
                                }
                                newTracks
                            }),
                    onNextNonContext = { tracks ->
                        extendTrackList = tracks.sortedByDescending { it.track?.publishedAt }
                        playlist = extendTrackList?.map {
                            (it.channel to it.track).toAudioTrack()
                        }
                    },
                    onErrorWithContext = onError,
                    onCompleteWithContext = onComplete
            )
    )

    fun loadTracksFromRemote(onError: ((IMvpView?, Throwable) -> Unit)? = null, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = SplashPresenter.allUpdateObservable,
                    triggerProgress = false,
                    onErrorWithContext = onError,
                    onCompleteWithContext = {
                        loadTracks(false, onError, onComplete)
                    }
            )
    )

    fun updateFavouriteTracks(extendTrack: ExtendTrackModel, body: LikeModel, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.updateFavouriteTrack(extendTrack.id?.toInt()!!, body),
                    triggerProgress = false,
                    onNextNonContext = {
                        updatedTrack = it
                        extendTrack.like?.let {
                            it.likeCounts = updatedTrack?.likeCount
                            it.isLiked = !it.isLiked
                            TrackManager.updateFavouriteTrack(it)
                        }

                    },
                    onCompleteWithContext = onComplete
            )

    )

}