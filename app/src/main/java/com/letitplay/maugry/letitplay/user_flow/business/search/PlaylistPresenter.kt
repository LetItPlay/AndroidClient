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
                            ChannelManager.getExtendChannel(),
                            TrackManager.getLastTracksWithTag(tag),
                            BiFunction { channels: List<ExtendChannelModel>, extTracks: List<ExtendTrackModel> ->
                                var totalTime = 0
                                val tracksInPlaylist: MutableList<TrackModel> = mutableListOf()
                                for (extTrack in extTracks) {
                                    val lang = extTrack.track?.lang?.let { lang -> ContentLanguage.getLanguage(lang) }
                                    val trackDuration = extTrack.track?.totalLengthInSeconds!!
                                    if (trackDuration == 0 || currentContentLang != lang) continue
                                    if (trackDuration + totalTime <= PLAYLIST_DURATION) {
                                        tracksInPlaylist.add(extTrack.track!!)
                                        totalTime += trackDuration
                                    }
                                    if (totalTime >= PLAYLIST_DURATION) {
                                        break
                                    }
                                }
                                val tracksWithChannels: List<AudioTrack> = tracksInPlaylist.map { track ->
                                    val channel = channels.first { it.id == track.stationId }
                                    (channel.channel!! to track).toAudioTrack()
                                }
                                PlaylistModel("Актуальные новости за 30 минут",
                                        "Подборка актуальных новостей в виде 30-минутного плейлиста",
                                        tracksWithChannels)
                            }),
                    onNextNonContext = { playlists = listOf(it) },
                    onCompleteWithContext = onComplete
            )
    )

    private const val PLAYLIST_DURATION = 30 * 60
}