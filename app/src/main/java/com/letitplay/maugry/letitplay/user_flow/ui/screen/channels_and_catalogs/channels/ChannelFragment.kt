package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalogs.channels

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.*
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalogs.ChannelAndCategoriesViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.Result
import com.letitplay.maugry.letitplay.utils.ext.defaultItemAnimator
import kotlinx.android.synthetic.main.channels_fragment.*
import timber.log.Timber


class ChannelFragment : BaseFragment(R.layout.channels_fragment) {

    private val channelsListAdapter = ChannelAdapter(::onChannelClick, ::onFollowClick)
    private val router by lazy { ServiceLocator.router }

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(ChannelAndCategoriesViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(vm)
        vm.channels.observe(this, Observer<Result<List<ChannelWithFollow>>> { result ->
            when (result) {
                is Result.Success -> {
                    channelsListAdapter.updateChannels(result.data)
                }
                is Result.Failure -> Timber.e(result.e)
            }
        })
        vm.isLoading.observe(this, Observer<Boolean>{
            when (it) {
                true -> showProgress()
                else -> hideProgress()
            }
        })
        vm.refreshing.observe(this, Observer<Boolean> {
            it?.let {
                swipe_refresh.isRefreshing = it
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val channelRecycler = view.findViewById<RecyclerView>(R.id.channels_list)
        channelRecycler.adapter = channelsListAdapter
        val listDivider = listDivider(channelRecycler.context, R.drawable.list_divider)
        channelRecycler.addItemDecoration(listDivider)
        channelRecycler.defaultItemAnimator.supportsChangeAnimations = false
        channelRecycler.setHasFixedSize(true)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            vm.onRefreshChannels()
        }
        return view
    }

    private fun onChannelClick(channel: Channel) {
        if (swipe_refresh.isRefreshing) return
        router.navigateTo(ChannelPageKey(channel.id))
    }

    private fun onFollowClick(channel: ChannelWithFollow) {
        if (swipe_refresh.isRefreshing) return
        vm.onFollowClick(channel)
    }
}