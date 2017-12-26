package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPresenter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelsAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.channels_fragment.*


class ChannelsFragment : BaseFragment<ChannelPresenter>(R.layout.channels_fragment, ChannelPresenter) {

    private var channelsListAdapter = ChannelsAdapter()


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.loadChannels({
            channels_list.apply {
                adapter = channelsListAdapter
                layoutManager = LinearLayoutManager(context)
            }
            channelsListAdapter.onClick = this::goToOtherView
            channelsListAdapter.setData(presenter.vm)
        })
    }

    private fun goToOtherView() {
    }

}