package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.Result
import com.letitplay.maugry.letitplay.utils.ext.loadCircularImage
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import kotlinx.android.synthetic.main.channel_small_item.view.*
import kotlinx.android.synthetic.main.trends_fragment.*
import timber.log.Timber


class TrendsFragment : BaseFragment(R.layout.trends_fragment) {

    private val trendsListAdapter by lazy {
        TrendAdapter(musicService,
                ::playTrack,
                ::onLikeClick)
    }

    private val channelsAdapter by lazy {
        ChannelsAdapter()
    }

    private val vm by lazy {
        ViewModelProviders.of(this, ServiceLocator.viewModelFactory)
                .get(TrendViewModel::class.java)
    }
    private var trendsRepo: MusicRepo? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(vm)
        vm.trends.observe(this, Observer<PagedList<TrackWithChannel>> {
            hideProgress()
            it?.let {
                trendsListAdapter.setList(it)
            }
        })
        vm.channels.observe(this, Observer<Result<List<Channel>>> { result ->
            when (result) {
                is Result.Success -> channelsAdapter.channels = result.data
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
        val channelsRecycler = view.findViewById<RecyclerView>(R.id.channelsRecyclerView)
        channelsRecycler.apply {
            adapter = channelsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(listDivider(context!!, R.drawable.list_transparent_divider_16dp, LinearLayoutManager.HORIZONTAL))
            isNestedScrollingEnabled = false
        }
        val seeAllButton = view.findViewById<View>(R.id.allChannelsText)
        seeAllButton.setOnClickListener {
            seeAllChannelsClick()
        }
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            vm.onRefresh()

            if (swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
        return view
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
        val playlist = (vm.trends.value)?.map(TrackWithChannel::toAudioTrack) ?: return
        trendsRepo = MusicRepo(playlist)
        navigationActivity.updateRepo(trackData.track.id, trendsRepo)
    }


    inner class ChannelsAdapter : RecyclerView.Adapter<ChannelsAdapter.ChannelsViewHolder>() {

        var channels: List<Channel> = emptyList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelsViewHolder {
            return ChannelsViewHolder(parent).apply {
                itemView.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onChannelClick(channels[adapterPosition])
                    }
                }
            }
        }

        override fun getItemCount(): Int = channels.size

        override fun onBindViewHolder(holder: ChannelsViewHolder, position: Int) {
            holder.update(channels[position])
        }

        inner class ChannelsViewHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.channel_small_item) {
            fun update(channel: Channel) {
                itemView.channel_icon.loadCircularImage(channel.imageUrl)
            }
        }
    }
}