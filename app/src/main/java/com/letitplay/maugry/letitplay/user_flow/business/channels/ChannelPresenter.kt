package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView


object ChannelPresenter : BasePresenter<IMvpView>() {

    var vm: List<ChannelModel>? = null

    fun loadChannels(onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.getChannels(),
                    onNextNonContext = {
                        vm = it
                    },
                    onCompleteWithContext = onComplete

            )
    )
}