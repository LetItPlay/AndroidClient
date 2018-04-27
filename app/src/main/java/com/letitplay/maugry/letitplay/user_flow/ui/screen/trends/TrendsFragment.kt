package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.*
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.ServiceLocator.router
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toAudioTrack
import com.letitplay.maugry.letitplay.data_management.repo.NetworkState
import com.letitplay.maugry.letitplay.data_management.repo.Status
import com.letitplay.maugry.letitplay.user_flow.business.feed.OnPlaylistActionsListener
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query.SearchResultsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.BeginSwipeHandler
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.hide
import com.letitplay.maugry.letitplay.utils.ext.show
import kotlinx.android.synthetic.main.trends_fragment.*
import timber.log.Timber


class TrendsFragment : BaseFragment(R.layout.trends_fragment) {

    private val trendsListAdapter by lazy {
        TrendAdapter(musicService,
                ::playTrack,
                ::onLikeClick,
                ::onOtherClick,
                ::onChannelTitleClick,
                swipeListener)
    }

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(TrendViewModel::class.java)
    }
    private var trendsRepo: MusicRepo? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(vm)
        vm.loadingState.observe(this, Observer<NetworkState> {
            when (it?.status) {
                Status.RUNNING -> {
                    showProgress()
                }
                Status.SUCCESS -> {
                    hideProgress()
                    trends_no_internet.hide()
                }
                Status.FAILED -> {
                    hideProgress()
                    trends_no_internet.show()
                }
            }
        })
        vm.trends.observe(this, Observer<PagedList<TrackWithChannel>> {
            it?.let {
                trendsListAdapter.submitList(it)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val trendsRecycler = view.findViewById<RecyclerView>(R.id.trend_list)
        trendsRecycler.apply {
            adapter = trendsListAdapter
            addItemDecoration(listDivider(trendsRecycler.context, R.drawable.list_divider))
            itemAnimator = DefaultItemAnimator().apply { supportsChangeAnimations = false }
        }
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            vm.onRefresh()
        }
        val beginSwipeHandler = BeginSwipeHandler(trendsRecycler)
        trendsListAdapter.onBeginSwipe = beginSwipeHandler::onSwipeBegin
        return view
    }

    override fun hideProgress() {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        } else {
            super.hideProgress()
        }
    }

    override fun showProgress() {
        if (swipe_refresh.isRefreshing) {
            return
        } else {
            super.showProgress()
        }
    }

    private fun onLikeClick(track: TrackWithChannel) {
        if (swipe_refresh.isRefreshing) return
        vm.onLikeClick(track)
    }

    private fun onOtherClick(track: TrackWithChannel, reason: Int) {
        if (swipe_refresh.isRefreshing) return
        vm.onReportClick(track, reason)
    }

    private fun onChannelTitleClick(trackData: TrackWithChannel) {
        if (swipe_refresh.isRefreshing) return
        router.navigateTo(ChannelPageKey(trackData.channel.id))
    }

    private fun playTrack(trackData: TrackWithChannel) {
        try {
            if (swipe_refresh?.isRefreshing == true) return
            var currentId = musicService?.musicRepo?.currentAudioTrack?.id
            val trackId = trackData.track.id
            vm.onListen(trackData.track)
            if (trendsRepo != null && musicService?.musicRepo?.getAudioTrackAtId(trackId) != null) {
                if (currentId != trackId)
                    navigationActivity.musicPlayerSmall?.skipToQueueItem(trackData.track.id)
                else navigationActivity.musicPlayerSmall?.playPause()
                return
            }
            val tracks = vm.trends.value
            val playlist = tracks?.map(TrackWithChannel::toAudioTrack)?.toMutableList()
                    ?: return
            trendsRepo = MusicRepo(playlist)
            navigationActivity.updateRepo(trackData.track.id, trendsRepo, tracks)
        } catch (e: Exception) {
            Timber.d("Trends")
        }
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