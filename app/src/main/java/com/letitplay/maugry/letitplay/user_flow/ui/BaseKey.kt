package com.letitplay.maugry.letitplay.user_flow.ui

import android.os.Parcelable


abstract class BaseKey : Parcelable {

    val fragmentTag: String
        get() = toString()

    fun newFragment(): BaseFragment {
        return createFragment()
    }

    abstract fun menuType(): MenuType

    abstract fun isRootFragment(): Boolean

    abstract fun createFragment(): BaseFragment

}