package com.letitplay.maugry.letitplay.user_flow.business.search

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.FeedManager
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.FeedModel
import com.letitplay.maugry.letitplay.data_management.model.PlaylistModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack


object PlaylistPresenter : BasePresenter<IMvpView>() {
    var playlists: List<PlaylistModel>? = null

    fun getPlaylists(onComplete: ((IMvpView?) -> Unit)) = execute(
            ExecutionConfig(
                    asyncObservable = FeedManager.getSearch(currentContentLang?.name?.toLowerCase() ?: "ru"),
                    onNextNonContext = { feedModel: FeedModel ->
                        val extendTracks = feedModel.tracks?.map {
                            val stationId = it.stationId
                            val trackId = it.id
                            ExtendTrackModel(trackId, it, feedModel.channels?.find { it.id == stationId })
                        } ?: emptyList()
                        val tracksWithChannels: List<AudioTrack> = extendTracks.map { track ->
                            (track.channel!! to track.track).toAudioTrack()
                        }
                        var title = "Актуальные новости за 30 минут"
                        var subTitle = "Подборка актуальных новостей в виде 30-минутного плейлиста"

                        if (currentContentLang == ContentLanguage.EN) {
                            title = "Fresh news in 30 minutes"
                            subTitle = "A compilation of fresh news in one 30-minute playlist"
                        }

                        playlists = listOf(PlaylistModel(title,
                                subTitle,
                                tracksWithChannels))
                    },
                    onCompleteWithContext = onComplete
            )
    )
}