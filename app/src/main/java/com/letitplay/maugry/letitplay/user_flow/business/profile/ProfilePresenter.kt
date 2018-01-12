package com.letitplay.maugry.letitplay.user_flow.business.profile

import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


object ProfilePresenter : BasePresenter<IMvpView>() {

    var tracksList: List<TrackModel>? = null

    fun loadFavouriteTracks(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            TrackManager.getTracks(),
                            TrackManager.getFavouriteTracks(),
                            BiFunction { tracks: List<TrackModel>, favouriteTracks: List<FavouriteTracksModel> ->
                                Pair(tracks, favouriteTracks)
                            }
                    ),
                    onNextNonContext = { (tracks, favouriteTracks) ->
                        tracksList = tracks.filter {
                            val id = it.id
                            favouriteTracks.find { it.id == id && it.isLiked} != null
                        }
                    },
                    onCompleteWithContext = onComplete
            )
    )

}