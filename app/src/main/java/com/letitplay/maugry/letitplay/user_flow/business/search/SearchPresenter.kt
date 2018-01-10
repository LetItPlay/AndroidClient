package com.letitplay.maugry.letitplay.user_flow.business.search

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object SearchPresenter : BasePresenter<IMvpView>() {
    var queryResult: Pair<List<ChannelModel>, List<TrackModel>> = Pair(emptyList(), emptyList())

    fun executeQuery(query: String, onComplete: ((IMvpView?) -> Unit)) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.queryChannels(query),
                            TrackManager.queryTracks(query),
                            BiFunction { channels: List<ChannelModel>, tracks: List<TrackModel> ->
                                Pair(channels, tracks)
                            }),
                    onNextNonContext = { queryResult = it },
                    onCompleteWithContext = onComplete
            )
    )
}