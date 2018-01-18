package com.letitplay.maugry.letitplay.user_flow.business.Splash

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers


object SplashPresenter : BasePresenter<IMvpView>() {

    val allUpdateObservable = Observable.zip(
            TrackManager.getTracks(),
            ChannelManager.getChannels(),
            TrackManager.getFavouriteTracks(),
            ChannelManager.getFollowingChannels(),
            Function4
            { tracks: List<TrackModel>, channel: List<ChannelModel>, favouriteTrack: List<FavouriteTracksModel>, followingChannels: List<FollowingChannelModel> ->
                LanguageViewModel(tracks, channel, favouriteTrack, followingChannels)
            })
            .observeOn(Schedulers.io())
            .doOnNext {
                model ->
                val extendTrackList: List<ExtendTrackModel> = model.tracks.map {
                    val stationId = it.stationId
                    val trackId = it.id
                    ExtendTrackModel(trackId, it, model.channel.find { it.id == stationId },
                            model.favouriteTrack.find { it.id == trackId })
                }
                TrackManager.updateExtendTrackModel(extendTrackList)
                val extendChannelList: List<ExtendChannelModel> = model.channel.map {
                    val id = it.id
                    ExtendChannelModel(it.id, it, model.followingChannels.find { it.id == id }
                    )
                }
                ChannelManager.updateExtendChannel(extendChannelList)
            }

    fun loadData(onComplete: ((IMvpView?) -> Unit)? = null) =
            execute(
                    ExecutionConfig(
                            asyncObservable = allUpdateObservable,
                            triggerProgress = false,
                            onCompleteWithContext = onComplete
                    )
            )

    class LanguageViewModel(var tracks: List<TrackModel>, var channel: List<ChannelModel>,
                            var favouriteTrack: List<FavouriteTracksModel>,
                            var followingChannels: List<FollowingChannelModel>
    )
}