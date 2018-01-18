package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.search.ResultItem
import com.letitplay.maugry.letitplay.user_flow.business.search.SearchPresenter
import com.letitplay.maugry.letitplay.user_flow.business.search.SearchResultsAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageKey


class SearchFragment : BaseFragment<SearchPresenter>(R.layout.search_fragment, SearchPresenter) {

    private val resultsAdapter = SearchResultsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            resultsAdapter.onChannelClick = this::toChannel
            resultsAdapter.onTrackClick = this::playTrack
            resultsAdapter.onFollowClick = this::updateFollowers
            resultsAdapter.musicService = musicService
            val resultsRecyclerView = it.findViewById<RecyclerView>(R.id.results_recycler)
            resultsRecyclerView?.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = resultsAdapter
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.lastQuery?.let {
            performQuery(it)
        }
    }

    private fun updateFollowers(channelItem: ExtendChannelModel, isFollow: Boolean, position: Int) {

        val followerModel: FollowersModel = if (isFollow) FollowersModel(1)
        else FollowersModel(-1)

        channelItem.channel?.id?.let {
            presenter?.updateChannelFollowers(it, followerModel) {
                presenter.updatedChannel?.let {
                    val channel: FollowingChannelModel = FollowingChannelModel().query { it.equalTo("id", channelItem.channel?.id) }.first()
                    channel.isFollowing = !isFollow
                    channel.save()
                    channelItem.following = channel
                    resultsAdapter.notifyItemChanged(position)
                }
            }
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

    override fun onDestroy() {
        super.onDestroy()
        presenter?.lastQuery = null
    }

    private fun performQuery(query: String) {
        presenter?.executeQuery(query) {
            val (channels, tracks) = presenter.queryResult
            resultsAdapter.data = channels
                    .map(ResultItem::ChannelItem)
                    .plus(tracks.map(ResultItem::TrackItem))
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu_item, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchItem.expandActionView()
        presenter?.lastQuery?.let {
            searchView.setQuery(it, true)
            searchView.clearFocus()
        }
        activity?.let { activity ->
            val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                performQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter?.lastQuery = newText
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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    private fun goBack() {
        navigationActivity.backstackDelegate.onBackPressed()
    }
}