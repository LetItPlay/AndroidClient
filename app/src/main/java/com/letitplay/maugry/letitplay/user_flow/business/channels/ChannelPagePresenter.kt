package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.data_management.manager.TrackManager
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView


object ChannelPagePresenter : BasePresenter<IMvpView>() {

    var vm: List<TrackModel>? = null

    fun loadTracks(id: Int, onComplete: ((IMvpView?) -> Unit)? = null) = ChannelPagePresenter.execute(
            ExecutionConfig(
                    asyncObservable = TrackManager.getTracks(id),
                    onNextNonContext = {
                        vm = it
                    },
                    onCompleteWithContext = onComplete

            )
    )
}