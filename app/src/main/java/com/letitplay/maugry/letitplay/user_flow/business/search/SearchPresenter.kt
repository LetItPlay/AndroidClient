package com.letitplay.maugry.letitplay.user_flow.business.search

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object SearchPresenter : BasePresenter<IMvpView>() {
    var queryResult: Pair<List<ExtendChannelModel>, List<AudioTrack>> = Pair(emptyList(), emptyList())
    var lastQuery: String? = null
    var updatedChannel: ChannelModel? = null

    fun executeQuery(query: String, onComplete: ((IMvpView?) -> Unit)) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.queryChannels(query)
                                    .map {  channels ->
                                        channels.filter {
                                            val lang = it.channel?.lang?.let { lang -> ContentLanguage.getLanguage(lang) }
                                            currentContentLang == lang
                                        }
                                    },
                            TrackManager.queryTracks(query, currentContentLang),
                            BiFunction
                            { foundedChannels: List<ExtendChannelModel>, foundedTracks: List<AudioTrack> ->

                                foundedChannels to foundedTracks
                            }),
                    onNextNonContext = { queryResult = it },
                    onCompleteWithContext = onComplete
            )
    )

    fun updateChannelFollowers(id: Int, body: UpdateFollowersRequestBody, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.updateChannelFollowers(id, body),
                    onNextNonContext = {
                        updatedChannel = it
                    },
                    onCompleteWithContext = onComplete
            )
    )
}