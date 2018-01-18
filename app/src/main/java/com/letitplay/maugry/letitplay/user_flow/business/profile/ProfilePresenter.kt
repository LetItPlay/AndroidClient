package com.letitplay.maugry.letitplay.user_flow.business.profile

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView


object ProfilePresenter : BasePresenter<IMvpView>() {

    var extendTrackList: List<ExtendTrackModel>? = null
    var playlist: List<AudioTrack>? = null

    fun loadFavouriteTracks(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.getFavouriteExtendTrack(),
                    onNextNonContext = {
                        extendTrackList = it
                        playlist = extendTrackList?.map {
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

}