package com.letitplay.maugry.letitplay.user_flow.business.search

import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.PlaylistModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView


object PlaylistPresenter : BasePresenter<IMvpView>() {
    var playlists: List<PlaylistModel> = emptyList()

    fun getPlaylists(onComplete: ((IMvpView?) -> Unit)) = execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.getTracksWithTag("новости").map { tracks ->
                        var totalTime = 0
                        val tracksInPlaylist: MutableList<TrackModel> = mutableListOf()
                        for (track in tracks) {
                            val trackDuration = track.audio_file!!.length_seconds!!
                            if (trackDuration + totalTime < PLAYLIST_DURATION) {
                                tracksInPlaylist.add(track)
                                totalTime += trackDuration
                            }
                            if (trackDuration < PLAYLIST_DURATION) {
                                tracksInPlaylist.add(track)
                            }
                        }
                        PlaylistModel("Новости за 30 минут", tracksInPlaylist)
                    },
                    onNextNonContext = { playlists = listOf(it) },
                    onCompleteWithContext = onComplete
            )
    )

    private const val PLAYLIST_DURATION = 30 * 60
}