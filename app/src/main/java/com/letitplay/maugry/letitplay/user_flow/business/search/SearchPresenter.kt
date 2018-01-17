package com.letitplay.maugry.letitplay.user_flow.business.search

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.utils.toAudioTrack
import io.reactivex.Observable
import io.reactivex.functions.Function3


object SearchPresenter : BasePresenter<IMvpView>() {
    var queryResult: Pair<List<ChannelModel>, List<AudioTrack>> = Pair(emptyList(), emptyList())

    fun executeQuery(query: String, onComplete: ((IMvpView?) -> Unit)) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.queryChannels(query),
                            TrackManager.queryTracks(query),
                            ChannelManager.getChannels(), Function3<List<ChannelModel>, List<TrackModel>, List<ChannelModel>, Pair<List<ChannelModel>, List<AudioTrack>>> {
                        foundedChannels: List<ChannelModel>, foundedTracks: List<TrackModel>, channels: List<ChannelModel> ->
                            val audioTracks = foundedTracks
                                    .map { track ->
                                        val channel = channels.first { channel -> channel.id == track.stationId }
                                        Pair(channel, track)
                                    }
                                    .map(Pair<ChannelModel, TrackModel>::toAudioTrack)
                        foundedChannels to audioTracks
                    }),
                    onNextNonContext = { queryResult = it },
                    onCompleteWithContext = onComplete
            )
    )
}