package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.search.PlaylistAdapter
import com.letitplay.maugry.letitplay.user_flow.business.search.PlaylistPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.playlist_fragment.*
import timber.log.Timber


class PlaylistFragment : BaseFragment<PlaylistPresenter>(R.layout.playlist_fragment, PlaylistPresenter) {

    private val playlistAdapter = PlaylistAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_list.apply {
            adapter = playlistAdapter
            layoutManager = LinearLayoutManager(context)
        }
        presenter?.getPlaylists {
            presenter?.playlists?.let {
                playlistAdapter.setData(it)
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