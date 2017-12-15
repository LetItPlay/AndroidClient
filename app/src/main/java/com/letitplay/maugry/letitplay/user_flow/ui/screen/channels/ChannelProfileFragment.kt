package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelProfileAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import kotlinx.android.synthetic.main.channel_profile_fragment.*

class ChannelProfileFragment : BaseFragment(R.layout.channel_profile_fragment) {

    private val channelsProfileAdapter = ChannelProfileAdapter()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recent_tracks_list.apply {
            adapter = channelsProfileAdapter
            layoutManager = LinearLayoutManager(context)
        }
        channelsProfileAdapter.onClick = this::goToOtherView
        channelsProfileAdapter.setData(arrayListOf(ChannelModel(), ChannelModel(), ChannelModel(), ChannelModel()))
    }
    private fun goToOtherView() {
        (activity as NavigationActivity).navigateTo(ChannelProfileKey())
    }

}
