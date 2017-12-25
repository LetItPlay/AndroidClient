package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelsAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.channels_fragment.*


class ChannelsFragment : BaseFragment(R.layout.channels_fragment) {

    private var channelsListAdapter = ChannelsAdapter()


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channels_list.apply {
            adapter = channelsListAdapter
            layoutManager = LinearLayoutManager(context)
        }
        channelsListAdapter.onClick = this::goToOtherView
        channelsListAdapter.setData(arrayListOf(ChannelModel(), ChannelModel(), ChannelModel(), ChannelModel()))
    }

    private fun goToOtherView() {
    }

}