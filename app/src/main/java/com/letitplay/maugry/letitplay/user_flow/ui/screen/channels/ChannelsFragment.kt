package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPresenter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelsAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import kotlinx.android.synthetic.main.channels_fragment.*


class ChannelsFragment : BaseFragment<ChannelPresenter>(R.layout.channels_fragment, ChannelPresenter) {

    private var channelsListAdapter = ChannelsAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.loadChannels({
            channels_list.apply {
                adapter = channelsListAdapter
                layoutManager = LinearLayoutManager(context)
            }
            channelsListAdapter.onClick = this::goToOtherView
            channelsListAdapter.onFollowClick = this::updateFollowers
            presenter.channelList?.let {
                channelsListAdapter.data = it
            }
        })
    }

    private fun goToOtherView(id: Int?) {
        id?.let {
            (activity as NavigationActivity).navigateTo(ChannelPageKey(id))
        }
    }

    private fun updateFollowers(channelId: Int?, isFollow: Boolean) {

        var followerModel: FollowersModel
        if (isFollow) followerModel = FollowersModel(1)
        else followerModel = FollowersModel(-1)

        channelId?.let {
            presenter?.updateChannelFollowers(channelId, followerModel) {
                presenter.updatedChannel?.let {
                    var channel: FollowingChannelModel = FollowingChannelModel().query { it.equalTo("id", channelId) }.first()
                    channel.isFollowing = !isFollow
                    channel.save()
                    presenter.channelList?.let {
                        channelsListAdapter.data = it
                    }
                }
            }
        }
    }

}