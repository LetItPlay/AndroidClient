package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.support.v4.app.Fragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.zhuinden.simplestack.StateChange


class FragmentStateChanger(val fragmentManager: android.support.v4.app.FragmentManager, val containerId: Int) {

    fun handleStateChange(stateChange: StateChange) {

        val fragmentTransaction: android.support.v4.app.FragmentTransaction = fragmentManager.beginTransaction().disallowAddToBackStack()

//        if (stateChange.direction == StateChange.FORWARD) {
//        fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_left)
//        }

        val previousState: List<BaseKey> = stateChange.getPreviousState()
        val newState: List<BaseKey> = stateChange.getNewState()

        previousState.forEach {
            var fragment: Fragment = fragmentManager.findFragmentByTag(it.getFragmentTag())
            if (!newState.contains(it)) {
                fragmentTransaction.remove(fragment)
            } else if (!fragment.isDetached) fragmentTransaction.detach(fragment)
        }

        newState.forEach {
            var fragment = fragmentManager.findFragmentByTag(it.getFragmentTag())
            if (it.equals(stateChange.topNewState())) {
                if (fragment != null) {
                    if (fragment.isDetached) {
                        fragmentTransaction.attach(fragment)
                    }
                } else {
                    fragment = it.newFragment()
                    fragmentTransaction.add(containerId, fragment, it.getFragmentTag())
                }
            } else {
                if (fragment != null && !fragment.isDetached) fragmentTransaction.detach(fragment)
            }
        }

        fragmentTransaction.commitNow()
    }
}