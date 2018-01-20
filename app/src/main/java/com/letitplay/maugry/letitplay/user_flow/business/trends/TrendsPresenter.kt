package com.letitplay.maugry.letitplay.user_flow.business.trends

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.business.Splash.SplashPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView

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
                        val sortedTracks = it.sortedByDescending { it.like?.likeCounts }
                                .filter {
                                    val lang = it.track?.lang?.let { lang -> ContentLanguage.getLanguage(lang) }
                                    return@filter currentContentLang == lang
                                }
                        extendTrackList = sortedTracks
                        playlist = sortedTracks.map {
                            AudioTrack(
                                    id = it.track?.id!!,
                                    url = "${GL_MEDIA_SERVICE_URL}${it.track?.audio?.fileUrl}",
                                    title = it.track?.name,
                                    subtitle = it.channel?.name,
                                    imageUrl = "${GL_MEDIA_SERVICE_URL}${it.track?.image}",
                                    channelTitle = it.channel?.name,
                                    length = it.track?.audio?.lengthInSeconds,
                                    listenCount = it.track?.listenCount,
                                    publishedAt = it.track?.publishedAt
                            )
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
}