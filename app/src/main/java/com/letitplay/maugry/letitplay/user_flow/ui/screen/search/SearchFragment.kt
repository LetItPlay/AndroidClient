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
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.search.ResultItem.ChannelItem
import com.letitplay.maugry.letitplay.user_flow.business.search.ResultItem.TrackItem
import com.letitplay.maugry.letitplay.user_flow.business.search.SearchPresenter
import com.letitplay.maugry.letitplay.user_flow.business.search.SearchResultsAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import kotlinx.android.synthetic.main.search_fragment.*


class SearchFragment : BaseFragment<SearchPresenter>(R.layout.search_fragment, SearchPresenter), SearchView.OnQueryTextListener {

    private val resultsAdapter = SearchResultsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        results_recycler?.apply {
            adapter = resultsAdapter
            layoutManager = LinearLayoutManager(context)
        }
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
        searchView.setOnQueryTextListener(this)
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
        (activity as NavigationActivity).backstackDelegate.backstack.goBack()
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        presenter?.executeQuery(query) {
            val (channels, tracks) = presenter.queryResult
            resultsAdapter.data = channels
                    .map(::ChannelItem)
                    .plus(tracks.map(::TrackItem))
        }
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return true
    }
}