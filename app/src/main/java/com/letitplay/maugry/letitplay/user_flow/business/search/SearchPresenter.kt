package com.letitplay.maugry.letitplay.user_flow.business.search

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelItemModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object SearchPresenter : BasePresenter<IMvpView>() {
    var queryResult: Pair<List<ChannelItemModel>, List<AudioTrack>> = Pair(emptyList(), emptyList())
    var lastQuery: String? = null

    fun executeQuery(query: String, onComplete: ((IMvpView?) -> Unit)) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.queryChannels(query),
                            TrackManager.queryTracks(query),
                            BiFunction<List<ChannelItemModel>, List<AudioTrack>, Pair<List<ChannelItemModel>, List<AudioTrack>>> {
                                foundedChannels: List<ChannelItemModel>, foundedTracks: List<AudioTrack> ->
                                foundedChannels to foundedTracks
                            }),
                    onNextNonContext = { queryResult = it },
                    onCompleteWithContext = onComplete
            )
    )
}