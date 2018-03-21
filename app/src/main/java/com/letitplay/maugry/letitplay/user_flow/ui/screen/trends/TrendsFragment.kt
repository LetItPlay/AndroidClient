package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toAudioTrack
import com.letitplay.maugry.letitplay.user_flow.business.feed.OnPlaylistActionsListener
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.BeginSwipeHandler
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.Result
import kotlinx.android.synthetic.main.trends_fragment.*
import timber.log.Timber


class TrendsFragment : BaseFragment(R.layout.trends_fragment) {

    private val trendsListAdapter by lazy {
        TrendAdapter(musicService,
                ::playTrack,
                ::onLikeClick,
                swipeListener,
                ::onChannelClick,
                ::seeAllChannelsClick)
    }

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(TrendViewModel::class.java)
    }
    private var trendsRepo: MusicRepo? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(vm)
        vm.trends.observe(this, Observer<PagedList<TrackWithChannel>> {
            hideProgress()
            it?.let {
                trendsListAdapter.submitList(it)
            }
        })
        vm.channels.observe(this, Observer<Result<List<Channel>>> { result ->
            when (result) {
                is Result.Success -> trendsListAdapter.updateChannels(result.data)
                is Result.Failure -> Timber.e(result.e)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val trendsRecycler = view.findViewById<RecyclerView>(R.id.trend_list)
        trendsRecycler.apply {
            adapter = trendsListAdapter
            addItemDecoration(listDivider(trendsRecycler.context, R.drawable.list_divider))
            itemAnimator = DefaultItemAnimator().apply { supportsChangeAnimations = false }
            isNestedScrollingEnabled = false
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

    private fun onLikeClick(track: TrackWithChannel) {
        if (swipe_refresh.isRefreshing) return
        vm.onLikeClick(track)
    }

    private fun onChannelClick(channel: Channel) {
        navigationActivity.navigateTo(ChannelPageKey(channel.id))
    }

    private fun seeAllChannelsClick() {
        navigationActivity.navigateTo(ChannelsKey())
    }

    private fun playTrack(trackData: TrackWithChannel) {
        if (swipe_refresh.isRefreshing) return
        val trackId = trackData.track.id
        vm.onListen(trackData.track)
        if (trendsRepo != null && trendsRepo?.getAudioTrackAtId(trackId) != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackData.track.id)
            return
        }
        val playlist = (vm.trends.value)?.map(TrackWithChannel::toAudioTrack)?.toMutableList() ?: return
        trendsRepo = MusicRepo(playlist)
        navigationActivity.updateRepo(trackData.track.id, trendsRepo)
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
}