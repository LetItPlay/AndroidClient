package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.Result
import com.letitplay.maugry.letitplay.utils.ext.defaultItemAnimator
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import kotlinx.android.synthetic.main.channels_fragment.*
import timber.log.Timber


class TrendsFragment : BaseFragment(R.layout.trends_fragment) {

    private val trendsListAdapter by lazy {
        TrendAdapter(musicService,
                ::playTrack,
                ::onLikeClick,
                ::onChannelClick,
                ::seeAllChannelsClick)
    }

    private val vm by lazy {
        ViewModelProviders.of(this, ServiceLocator.viewModelFactory)
                .get(TrendViewModel::class.java)
    }
    private val preferenceHelper by lazy { PreferenceHelper(context!!) }
    private var trendsRepo: MusicRepo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.trends.observe(this, Observer<Result<List<TrackWithChannel>>> { result ->
            when (result) {
                is Result.Success -> trendsListAdapter.data = result.data
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
        trendsRecycler.adapter = trendsListAdapter
        trendsRecycler.addItemDecoration(listDivider(trendsRecycler.context, R.drawable.list_divider))
        trendsRecycler.defaultItemAnimator.supportsChangeAnimations = false
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

        if (!preferenceHelper.isListened(trackData.track.id)) {
            vm.sendListen()
            preferenceHelper.saveListened(trackData.track.id)
        }

        if (trendsRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackData.track.id)
            return
        }
        val playlist = (vm.trends.value as Result.Success).data.map {
            it.toAudioTrack()
        }
        trendsRepo = MusicRepo(playlist)
        navigationActivity.updateRepo(trackData.track.id, trendsRepo)
    }

}