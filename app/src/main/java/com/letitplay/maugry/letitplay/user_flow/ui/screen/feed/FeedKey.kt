package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView


object FeedKey : BaseKey() {
    override fun isRootFragment(): Boolean = true

    override fun createFragment(): BaseFragment<BasePresenter<IMvpView>> = FeedFragment()
}