package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.support.v4.app.Fragment
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.MenuType
import com.zhuinden.simplestack.StateChange


class FragmentStateChanger(val fragmentManager: android.support.v4.app.FragmentManager, val containerId: Int) {

    fun handleStateChange(stateChange: StateChange, menuType: MenuType) {

        fragmentManager.beginTransaction().apply {

//            when (menuType) {
//                MenuType.PLAYER ->
//                    when (stateChange.direction) {
//                        StateChange.FORWARD -> {
//                            setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top, R.anim.slide_in_from_bottom, R.anim.slide_out_to_top)
//                        }
//                        StateChange.BACKWARD -> {
//                            setCustomAnimations(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom, R.anim.slide_in_from_top, R.anim.slide_out_to_bottom)
//                        }
//                    }
//            }

            val previousState: List<BaseKey> = stateChange.getPreviousState()
            val newState: List<BaseKey> = stateChange.getNewState()

            previousState.forEach {
                var fragment: Fragment = fragmentManager.findFragmentByTag(it.fragmentTag)
                if (!newState.contains(it)) {
                    remove(fragment)
                } else if (!fragment.isDetached) {
                    detach(fragment)
                }
            }

            newState.forEach {
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
        }.commitNow()
    }
}