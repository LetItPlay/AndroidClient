package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import kotlinx.android.synthetic.main.feed_fragment.*

class FeedFragment : BaseFragment(R.layout.feed_fragment) {

    private val feedListAdapter = FeedAdapter()


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feed_list.apply {
            adapter = feedListAdapter
            layoutManager = LinearLayoutManager(context)
        }
        feedListAdapter.onClick = this::goToOtherView
        feedListAdapter.setData(arrayListOf(ChannelModel(), ChannelModel(), ChannelModel(), ChannelModel()))
    }

    private fun goToOtherView() {
        (activity as NavigationActivity).navigateTo(TrackKey)
    }

}
