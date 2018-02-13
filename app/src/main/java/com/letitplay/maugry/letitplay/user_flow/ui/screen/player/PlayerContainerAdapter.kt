package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


class PlayerContainerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    enum class PlayerTabs {
        PLAYER,
        TRACKS
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            PlayerTabs.PLAYER.ordinal -> PlayerFragment()
            PlayerTabs.TRACKS.ordinal -> TrackFragment()
            else -> null
        }
    }

    override fun getCount(): Int = PlayerTabs.values().size
}