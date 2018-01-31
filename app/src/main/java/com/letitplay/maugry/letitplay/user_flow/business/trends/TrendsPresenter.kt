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
import com.letitplay.maugry.letitplay.utils.toAudioTrack
import org.joda.time.DateTime
import org.joda.time.Days

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
                        val now = DateTime.now()
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

    private fun List<ExtendTrackModel>.takeLastDate(now: DateTime): List<ExtendTrackModel> =
            sortedByDescending {
                it.track?.publishedAt
            }.filter {
                it.track?.publishedAt != null
            }.takeWhile {
                val publish = DateTime(it.track?.publishedAt?.time!!)
                val days = Days.daysBetween(publish, now).days
                 days in 0..8
            }


    private fun List<ExtendTrackModel>.sortByListenCount(): List<ExtendTrackModel> =
            sortedByDescending {
                it.track?.listenCount
            }

}