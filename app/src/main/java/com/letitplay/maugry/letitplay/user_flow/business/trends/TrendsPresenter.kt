package com.letitplay.maugry.letitplay.user_flow.business.trends

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.business.Splash.SplashPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import java.util.*

object TrendsPresenter : BasePresenter<IMvpView>() {

    var extendTrackList: List<ExtendTrackModel>? = null
    var playlist: List<AudioTrack>? = null
    var updatedTrack: TrackModel? = null

    fun loadTracks(triggerProgress: Boolean = true,
                   onError: ((IMvpView?, Throwable) -> Unit)? = null,
                   onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    triggerProgress = triggerProgress,
                    asyncObservable = TrackManager.getExtendTrack(),
                    onErrorWithContext = onError,
                    onNextNonContext = {
                        val now = Date(System.currentTimeMillis())
                        val sortedTracks = it
                                .filter {
                                    val lang = it.track?.lang?.let { lang -> ContentLanguage.getLanguage(lang) }
                                    return@filter currentContentLang == lang
                                }
                                .takeLastDate(now)
                                .sortByListenCount()

                        extendTrackList = sortedTracks
                        playlist = sortedTracks.map {
                            (it.channel to it.track).toAudioTrack()
                        }
                    },
                    onCompleteWithContext = onComplete
            )
    )

    fun loadTracksFromRemote(onError: ((IMvpView?, Throwable) -> Unit)? = null, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = SplashPresenter.allUpdateObservable,
                    triggerProgress = false,
                    onErrorWithContext = onError,
                    onCompleteWithContext = {
                        loadTracks(false, onError, onComplete)
                    }
            )
    )

    fun updateFavouriteTracks(id: Int, body: LikeModel, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.updateFavouriteTrack(id, body),
                    triggerProgress = false,
                    onNextNonContext = {
                        updatedTrack = it
                    },
                    onCompleteWithContext = onComplete
            )

    )

    private fun List<ExtendTrackModel>.takeLastDate(now: Date): List<ExtendTrackModel> =
            sortedByDescending {
                it.track?.publishedAt
            }.filter {
                it.track?.publishedAt != null
            }.takeWhile {
                val diff = now.time - it.track?.publishedAt?.time!!
                val diffDate = Date(diff)
                val cal = Calendar.getInstance()
                cal.time = diffDate
                val days = cal.get(Calendar.DAY_OF_MONTH)
                days < 8
            }


    private fun List<ExtendTrackModel>.sortByListenCount(): List<ExtendTrackModel> =
            sortedByDescending {
                it.track?.listenCount
            }

}