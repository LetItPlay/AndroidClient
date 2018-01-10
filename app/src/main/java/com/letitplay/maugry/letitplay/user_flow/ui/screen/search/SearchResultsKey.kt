package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView


object SearchResultsKey : BaseKey() {
    override fun isRootFragment() = false

    override fun createFragment(): BaseFragment<BasePresenter<IMvpView>> {
        return SearchFragment()
    }
}