package com.letitplay.maugry.letitplay.user_flow.ui


abstract class BaseKey {

    val fragmentTag: String
        get() = toString()

    fun newFragment(): BaseFragment {
        val fragment: BaseFragment = createFragment()
        return fragment
    }

    abstract fun isRootFragment(): Boolean

    abstract fun createFragment(): BaseFragment

}