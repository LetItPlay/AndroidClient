package com.letitplay.maugry.letitplay.user_flow.business.Splash

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.Function5
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import kotlin.system.measureTimeMillis


object SplashPresenter : BasePresenter<IMvpView>() {

    val allUpdateObservable = Observable.zip(
            TrackManager.getTracks(),
            ChannelManager.getChannels(),
            TrackManager.getFavouriteTracks(),
            ChannelManager.getFollowingChannels(),
            TrackManager.getListenedTracks(),
            Function5
            { tracks: List<TrackModel>, channel: List<ChannelModel>, favouriteTrack: List<FavouriteTracksModel>,
              followingChannels: List<FollowingChannelModel>, listenedTracks: List<ListenedTrackModel> ->
                LanguageViewModel(tracks, channel, favouriteTrack, followingChannels, listenedTracks)
            })
            .observeOn(Schedulers.io())
            .doOnNext { model ->
                measureTimeMillis {
                    val extendTrackList: List<ExtendTrackModel> = model.tracks.map {
                        val stationId = it.stationId
                        val trackId = it.id
                        ExtendTrackModel(trackId, it, model.channel.find { it.id == stationId })
                    }
               // TrackManager.updateExtendTrackModel(extendTrackList)
                    val extendChannelList: List<ExtendChannelModel> = model.channel.map {
                        val id = it.id
                        ExtendChannelModel(it.id, it, model.followingChannels.find { it.id == id }
                        )
                    }
               // ChannelManager.updateExtendChannel(extendChannelList)
                }.also {
                    Timber.d("DASHA"+it.toString())

                }
            }

    fun loadData(onComplete: ((IMvpView?) -> Unit)? = null) =
            execute(
                    ExecutionConfig(
                            asyncObservable = allUpdateObservable,
                            triggerProgress = false,
                            onCompleteWithContext = onComplete
                    )
            )

    class LanguageViewModel(var tracks: List<TrackModel>,
                            var channel: List<ChannelModel>,
                            var favouriteTrack: List<FavouriteTracksModel>,
                            var followingChannels: List<FollowingChannelModel>,
                            var listenedTracks: List<ListenedTrackModel>
    )
}