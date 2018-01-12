package com.letitplay.maugry.letitplay.user_flow.business.player

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.PlayerFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.TrackFragment


class PlayerContainerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val PAGE_COUNT = 2

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> PlayerFragment()
            1 -> TrackFragment()
            else -> null
        }
    }

    override fun getCount(): Int = PAGE_COUNT
}