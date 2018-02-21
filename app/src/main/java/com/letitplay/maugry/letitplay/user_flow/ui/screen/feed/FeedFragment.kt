package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.defaultItemAnimator
import com.letitplay.maugry.letitplay.utils.ext.hide
import com.letitplay.maugry.letitplay.utils.ext.show
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import kotlinx.android.synthetic.main.feed_fragment.*

class FeedFragment : BaseFragment(R.layout.feed_fragment) {
    private val vm by lazy {
        ViewModelProviders.of(this, ServiceLocator.viewModelFactory)
                .get(FeedViewModel::class.java)
    }

    private val feedListAdapter by lazy {
        FeedAdapter(musicService,
                ::onTrackClick,
                ::onLikeClick
        )
    }

    private var feedRepo: MusicRepo? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.feeds.observe(this, Observer<PagedList<TrackWithChannel>> {
            when (it?.size) {
                0 -> feed_no_tracks.show()
                else -> {
                    feed_no_tracks.hide()
                    feedListAdapter.setList(it)
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val feedRecycler = view.findViewById<RecyclerView>(R.id.feed_list)
        feedRecycler.adapter = feedListAdapter
        val divider = listDivider(feedRecycler.context, R.drawable.list_divider)
        feedRecycler.addItemDecoration(divider)
        feedRecycler.defaultItemAnimator.supportsChangeAnimations = false
        val goToChannels = view.findViewById<View>(R.id.go_to_channels)
        goToChannels.setOnClickListener {
            seeAllChannelsClick()
        }
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            vm.refreshFeed()

            if (swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
        return view
    }

    private fun seeAllChannelsClick() {
        navigationActivity.navigateTo(ChannelsKey())
    }

    private fun onLikeClick(trackData: TrackWithChannel) {
        if (swipe_refresh.isRefreshing) return
        vm.onLikeClick(trackData)
    }

    private fun onTrackClick(trackData: TrackWithChannel) {
        if (swipe_refresh.isRefreshing) return
        vm.onListen(trackData.track)
        if (feedRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackData.track.id)
            return
        }
        val playlist = (vm.feeds.value)!!.map {
            it.toAudioTrack()
        }
        feedRepo = MusicRepo(playlist)
        navigationActivity.updateRepo(trackData.track.id, feedRepo)
    }
}
