package com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.compilation.CompilationFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query.SearchResultsKey
import kotlinx.android.synthetic.main.navigation_main.*
import kotlinx.android.synthetic.main.playlists_fragment.*
import timber.log.Timber

class PlaylistsFragment : BaseFragment(R.layout.playlists_fragment) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationActivity.playlists_tabs.setupWithViewPager(playlists_pager)
        playlists_pager.adapter = PlaylistsAdapter(childFragmentManager)
    }

    inner class PlaylistsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            Timber.d("Navigate to results page")
            navigationActivity.navigateTo(SearchResultsKey())
        }
        return super.onOptionsItemSelected(item)
    }

}