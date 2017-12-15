package com.letitplay.maugry.letitplay.user_flow.ui

import android.os.Bundle
import android.os.Parcelable


abstract class BaseKey  {

    fun getFragmentTag(): String = toString()

    fun newFragment(): BaseFragment {
        val fragment: BaseFragment = createFragment()
        val bundle: Bundle = fragment.arguments ?: Bundle()
        bundle.putString("KEY", "key")
        fragment.arguments = bundle
        return fragment
    }

    abstract fun createFragment(): BaseFragment

}