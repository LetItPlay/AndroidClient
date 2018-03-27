package com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.compilation.CompilationFragment
import kotlinx.android.synthetic.main.playlists_fragment.*

class PlaylistsFragment: BaseFragment(R.layout.playlists_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlists_tabs.setupWithViewPager(playlists_pager)
        playlists_pager.adapter = PlaylistsAdapter(childFragmentManager)
    }

    inner class PlaylistsAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> UserPlaylistFragment()
                1 -> CompilationFragment()
                else -> throw IllegalStateException("No page at $position")
            }
        }

        override fun getCount(): Int = 2

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.my_playlist)
                1 -> getString(R.string.recommended)
                else -> throw IllegalStateException("No page at $position")
            }
        }
    }
}