package com.letitplay.maugry.letitplay.user_flow.ui

import android.os.Bundle
import paperparcel.PaperParcelable


abstract class BaseKey : PaperParcelable {

    val fragmentTag: String
        get() = toString()

    fun newFragment(): BaseFragment {
        val fragment: BaseFragment = createFragment()
        val bundle: Bundle = fragment.arguments ?: Bundle()
        bundle.putParcelable("KEY", this)
        fragment.arguments = bundle
        return fragment
    }

    abstract fun isRootFragment(): Boolean

    abstract fun createFragment(): BaseFragment

}