package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelAdapter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.channels_fragment.*


class ChannelsFragment : BaseFragment<ChannelPresenter>(R.layout.channels_fragment, ChannelPresenter) {

    private var channelsListAdapter = ChannelAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.loadChannels({
            channels_list.apply {
                adapter = channelsListAdapter
                layoutManager = LinearLayoutManager(context)
            }
            channelsListAdapter.onClick = this::goToOtherView
            channelsListAdapter.onFollowClick = this::updateFollowers
            presenter.extendChannelList?.let {
                channelsListAdapter.data = it
            }
        })
    }

    private fun goToOtherView(id: Int?) {
        id?.let {
            navigationActivity.navigateTo(ChannelPageKey(id))
        }
    }

    private fun updateFollowers(extendChannel: ExtendChannelModel, isFollow: Boolean, position: Int) {

        var followerModel: FollowersModel
        if (isFollow) followerModel = FollowersModel(1)
        else followerModel = FollowersModel(-1)

        extendChannel.channel?.id?.let {
            presenter?.updateChannelFollowers(extendChannel, followerModel) {
                presenter.updatedChannel?.let {
                    channelsListAdapter.notifyItemChanged(position)
                }
            }
        }
    }

}