package com.letitplay.maugry.letitplay.user_flow.business.profile

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack


object ProfilePresenter : BasePresenter<IMvpView>() {

    var extendTrackList: List<ExtendTrackModel>? = null
    var playlist: List<AudioTrack>? = null

    fun loadFavouriteTracks(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.getFavouriteExtendTrack(),
                    onNextNonContext = {
                        extendTrackList = it
                        playlist = extendTrackList?.map {
                            (it.channel to it.track).toAudioTrack()
                        }
                    },
                    onCompleteWithContext = onComplete
            )
    )

}