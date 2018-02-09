package com.letitplay.maugry.letitplay.user_flow.business.search

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object PlaylistPresenter : BasePresenter<IMvpView>() {
    var playlists: List<PlaylistModel>? = null

    fun getPlaylists(tag: String, onComplete: ((IMvpView?) -> Unit)) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            TrackManager.getTracks(),
                            ChannelManager.getChannels(),
                            BiFunction { tracks: List<TrackModel>, channels: List<ChannelModel> ->
                                val extTracks = tracks.map {
                                    val stationId = it.stationId
                                    ExtendTrackModel(it.id, it, channels.find { it.id == stationId })
                                }.filter { extendTrack ->
                                    val ret = extendTrack.channel?.tags?.any { it.contains(tag, true) } == true
                                    ret
                                }.filter { it.track?.lang == currentContentLang?.name?.toLowerCase() }
                                var totalTime = 0
                                val tracksInPlaylist: MutableList<ExtendTrackModel> = mutableListOf()
                                for (extTrack in extTracks) {
                                    val trackDuration = extTrack.track?.totalLengthInSeconds!!
                                    if (trackDuration == 0 || trackDuration>=240) continue
                                    if (trackDuration + totalTime <= PLAYLIST_DURATION) {
                                        tracksInPlaylist.add(extTrack)
                                        totalTime += trackDuration
                                    }
                                    if (totalTime >= PLAYLIST_DURATION) {
                                        break
                                    }
                                }
                                val tracksWithChannels: List<AudioTrack> = tracksInPlaylist.map { track ->
                                    (track.channel!! to track.track).toAudioTrack()
                                }
                                var title = "Актуальные новости за 30 минут"
                                var subTitle = "Подборка актуальных новостей в виде 30-минутного плейлиста"

                                if (currentContentLang == ContentLanguage.EN) {
                                    title = "Fresh news in 30 minutes"
                                    subTitle = "A compilation of fresh news in one 30-minute playlist"
                                }

                                PlaylistModel(title,
                                        subTitle,
                                        tracksWithChannels)
                            }),
                    onNextNonContext = { playlists = listOf(it) },
                    onCompleteWithContext = onComplete
            )
    )

    private const val PLAYLIST_DURATION = 30 * 60
}