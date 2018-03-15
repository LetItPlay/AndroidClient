package com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.SearchResultItem
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import kotlinx.android.synthetic.main.search_fragment.*


class SearchFragment : BaseFragment(R.layout.search_fragment) {

    private val resultsAdapter by lazy {
        SearchResultsAdapter(musicService, ::onChannelClick, ::onTrackClick, ::onChannelFollowClick)
    }

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(SearchViewModel::class.java)
    }

    private var resultsMusicRepo: MusicRepo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.isLoading.observe(this, Observer<Boolean> {
            when (it) {
                true -> showProgress()
                else -> hideProgress()
            }
        })
        vm.searchResult.observe(this, Observer<List<SearchResultItem>> {
            it?.let {
                if (it.size != resultsAdapter.itemCount) {
                    results_recycler?.scrollToPosition(0)
                }
                resultsAdapter.updateItems(it)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val resultsRecycler = view.findViewById<RecyclerView>(R.id.results_recycler)
        resultsRecycler.adapter = resultsAdapter
        val dividerItemDecoration = listDivider(resultsRecycler.context, R.drawable.list_divider)
        resultsRecycler.addItemDecoration(dividerItemDecoration)
        return view
    }

    private fun onChannelFollowClick(channelWithFollow: ChannelWithFollow) {
        vm.onChannelFollow(channelWithFollow)
    }

    private fun onChannelClick(channel: Channel) {
        navigationActivity.navigateTo(ChannelPageKey(channel.id))
    }

    private fun onTrackClick(track: AudioTrack) {
        val trackId = track.id
        vm.onListen(track)
        if (resultsMusicRepo != null && resultsMusicRepo?.getAudioTrackAtId(trackId) != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(track.id)
            return
        }
        val playlist = vm.searchResult.value!!
                .filterIsInstance(SearchResultItem.TrackItem::class.java)
                .map(SearchResultItem.TrackItem::track)
                .map(TrackWithChannel::toAudioTrack)
        resultsMusicRepo = MusicRepo(playlist.toMutableList())
        navigationActivity.updateRepo(track.id, resultsMusicRepo)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu_item, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchItem.expandActionView()
        if (vm.query.value != null) {
            searchView.setQuery(vm.query.value, true)
            searchView.clearFocus()
        }
        activity?.let { activity ->
            val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                vm.search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                vm.queryChanged(newText)
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
        navigationActivity.onBackPressed()
    }
}