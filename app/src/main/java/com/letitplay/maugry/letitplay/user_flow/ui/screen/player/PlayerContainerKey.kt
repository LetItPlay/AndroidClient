package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView


object PlayerContainerKey : BaseKey() {

    override fun isRootFragment(): Boolean = false

    override fun createFragment(): BaseFragment<BasePresenter<IMvpView>> = PlayerContainerFragment()
}