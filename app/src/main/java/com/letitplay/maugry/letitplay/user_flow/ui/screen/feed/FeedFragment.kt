package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.*
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toAudioTrack
import com.letitplay.maugry.letitplay.data_management.repo.NetworkState
import com.letitplay.maugry.letitplay.data_management.repo.Status
import com.letitplay.maugry.letitplay.user_flow.business.feed.OnPlaylistActionsListener
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query.SearchResultsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.BeginSwipeHandler
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.hide
import com.letitplay.maugry.letitplay.utils.ext.show
import kotlinx.android.synthetic.main.feed_fragment.*
import timber.log.Timber

class FeedFragment : BaseFragment(R.layout.feed_fragment) {
    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(FeedViewModel::class.java)
    }

    private val feedListAdapter by lazy {
        FeedAdapter(musicService,
                ::onTrackClick,
                ::onLikeClick,
                swipeListener
        )
    }

    private var feedRepo: MusicRepo? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.state.observe(this, Observer<FeedViewModel.ViewState> {
            it?.let {
                if (it.noChannels) {
                    feed_no_tracks.show()
                    feed_no_internet.hide()
                } else {
                    feed_no_tracks.hide()
                }
                feedListAdapter.submitList(it.data)
                feed_swipe_refresh?.isRefreshing = false
            }
        })
        vm.refreshState.observe(this, Observer<NetworkState> {
            when (it?.status) {
                Status.RUNNING -> showProgress()
                Status.SUCCESS -> {
                    feed_no_internet.hide()
                    hideProgress()
                }
                Status.FAILED -> {
                    feed_no_tracks.hide()
                    feed_no_internet.show()
                    hideProgress()
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val feedRecycler = view.findViewById<RecyclerView>(R.id.feed_list)
        feedRecycler.adapter = feedListAdapter
        val divider = listDivider(feedRecycler.context, R.drawable.list_divider)
        feedRecycler.addItemDecoration(divider)
        feedRecycler.itemAnimator = DefaultItemAnimator().apply { supportsChangeAnimations = false }
        val goToChannels = view.findViewById<View>(R.id.go_to_channels)
        goToChannels.setOnClickListener {
            seeAllChannelsClick()
        }
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.feed_swipe_refresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            vm.refreshFeed()
        }
        val beginSwipeHandler = BeginSwipeHandler(feedRecycler)
        feedListAdapter.onBeginSwipe = beginSwipeHandler::onSwipeBegin
        return view
    }

    private fun seeAllChannelsClick() {
        navigationActivity.replaceHistory(R.id.action_channels)
    }

    override fun showProgress() {
        if (feed_swipe_refresh.isRefreshing) {
            return
        } else {
            super.showProgress()
        }
    }

    override fun hideProgress() {
        if (feed_swipe_refresh.isRefreshing) {
            feed_swipe_refresh.isRefreshing = false
        } else {
            super.hideProgress()
        }
    }

    private fun onLikeClick(trackData: TrackWithChannel) {
        if (feed_swipe_refresh.isRefreshing) return
        vm.onLikeClick(trackData)
    }

    private fun onTrackClick(trackData: TrackWithChannel) {
        if (feed_swipe_refresh?.isRefreshing == true) return
        val trackId = trackData.track.id
        vm.onListen(trackData.track)
        if (feedRepo != null && feedRepo?.getAudioTrackAtId(trackId) != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackData.track.id)
            return
        }
        val tracks = vm.state.value?.data
        val playlist = tracks?.map(TrackWithChannel::toAudioTrack)?.toMutableList() ?: return
        feedRepo = MusicRepo(playlist)
        navigationActivity.updateRepo(trackData.track.id, feedRepo, tracks)
    }

    private val swipeListener: OnPlaylistActionsListener = object : OnPlaylistActionsListener {
        override fun performPushToBottom(trackData: TrackWithChannel): Boolean {
            vm.onSwipeTrackToBottom(trackData)
            navigationActivity.addTrackToEndRepo(trackData.toAudioTrack())
            return true
        }

        override fun performPushToTop(trackData: TrackWithChannel): Boolean {
            vm.onSwipeTrackToTop(trackData)
            navigationActivity.addTrackToStartRepo(trackData.toAudioTrack())
            return true
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
