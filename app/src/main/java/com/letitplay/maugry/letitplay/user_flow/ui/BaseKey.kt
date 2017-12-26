package com.letitplay.maugry.letitplay.user_flow.ui

import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter


abstract class BaseKey {

    val fragmentTag: String
        get() = toString()

    fun newFragment(): BaseFragment<BasePresenter<IMvpView>> {
        val fragment: BaseFragment<BasePresenter<IMvpView>> = createFragment()
        return fragment
    }

    abstract fun isRootFragment(): Boolean

    abstract fun createFragment(): BaseFragment<BasePresenter<IMvpView>>

}