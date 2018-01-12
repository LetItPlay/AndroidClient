package com.letitplay.maugry.letitplay.user_flow.ui

import android.os.Parcelable
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter


abstract class BaseKey : Parcelable {

    val fragmentTag: String
        get() = toString()

    fun newFragment(): BaseFragment<BasePresenter<IMvpView>> {
        val fragment: BaseFragment<BasePresenter<IMvpView>> = createFragment()
        return fragment
    }

    abstract fun isRootFragment(): Boolean

    abstract fun createFragment(): BaseFragment<BasePresenter<IMvpView>>

}