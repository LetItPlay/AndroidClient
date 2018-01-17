package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.search.ResultItem
import com.letitplay.maugry.letitplay.user_flow.business.search.SearchPresenter
import com.letitplay.maugry.letitplay.user_flow.business.search.SearchResultsAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageKey
import kotlinx.android.synthetic.main.search_fragment.*


class SearchFragment : BaseFragment<SearchPresenter>(R.layout.search_fragment, SearchPresenter) {

    private val resultsAdapter = SearchResultsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultsAdapter.onChannelClick = this::toChannel
        resultsAdapter.onTrackClick = this::playTrack
        results_recycler?.apply {
            adapter = resultsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun toChannel(channel: ChannelModel) {
        navigationActivity.navigateTo(ChannelPageKey(channel.id!!))
    }

    private fun playTrack(track: AudioTrack) {
        val tracks = resultsAdapter.data
                .filterIsInstance(ResultItem.TrackItem::class.java)
                .map(ResultItem.TrackItem::track)
        navigationActivity.updateRepo(track.id, MusicRepo(tracks))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu_item, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchItem.expandActionView()
        activity?.let { activity ->
            val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        }
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                presenter?.executeQuery(query) {
                    val (channels, tracks) = presenter.queryResult
                    resultsAdapter.data = channels
                            .map(ResultItem::ChannelItem)
                            .plus(tracks.map(ResultItem::TrackItem))
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean = true
            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_search) {
                    goBack()
                }
                return true
            }
        })
    }

    private fun goBack() {
        navigationActivity.backstackDelegate.onBackPressed()
    }
}