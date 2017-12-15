package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.support.v4.app.Fragment
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.zhuinden.simplestack.StateChange


class FragmentStateChanger(val fragmentManager: android.support.v4.app.FragmentManager, val containerId: Int) {

    fun handleStateChange(stateChange: StateChange) {

        fragmentManager.beginTransaction().apply {

            when (stateChange.direction) {
                StateChange.FORWARD -> {
                    setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                }
                StateChange.BACKWARD -> {
                    setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                }
            }

            val previousState = stateChange.getPreviousState<BaseKey>()
            val newState = stateChange.getNewState<BaseKey>()

            previousState.forEach {
                var fragment: Fragment = fragmentManager.findFragmentByTag(it.fragmentTag)
                if (!newState.contains(it)) {
                    remove(fragment)
                } else if (!fragment.isDetached) {
                    detach(fragment)
                }
            }

            newState.forEach {
                val s: String = it.fragmentTag
                var fragment = fragmentManager.findFragmentByTag(it.fragmentTag)
                if (it == stateChange.topNewState<Any>()) {
                    if (fragment != null) {
                        if (fragment.isDetached) {
                            attach(fragment)
                        }
                    } else {
                        fragment = it.newFragment()
                        add(containerId, fragment, it.fragmentTag)
                    }
                } else {
                    if (fragment != null && !fragment.isDetached) detach(fragment)
                }
            }

            commitNow()
        }
    }
}