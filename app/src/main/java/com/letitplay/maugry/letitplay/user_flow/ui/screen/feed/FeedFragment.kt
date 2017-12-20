package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedChannelsAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelProfileKey
import kotlinx.android.synthetic.main.feed_fragment.*

class FeedFragment : BaseFragment(R.layout.feed_fragment) {

    private val channelsListAdapter = FeedChannelsAdapter()


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feed_channels_list.apply {
            adapter = channelsListAdapter
            layoutManager = LinearLayoutManager(context)
        }
        channelsListAdapter.onClick = this::goToOtherView
        channelsListAdapter.setData(arrayListOf(ChannelModel(), ChannelModel(), ChannelModel(), ChannelModel()))
    }

    private fun goToOtherView() {
        (activity as NavigationActivity).navigateTo(FeedTestKey())
    }

}
