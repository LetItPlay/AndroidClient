package com.letitplay.maugry.letitplay.user_flow.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment(open val layoutId: Int) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(layoutId, container, false)
    }

    fun <T> getKey(): T where T : BaseKay = arguments.getParcelable("KEY")
}
