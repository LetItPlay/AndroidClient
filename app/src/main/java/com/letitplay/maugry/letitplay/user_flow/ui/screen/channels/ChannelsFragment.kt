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
        channels_list.apply {
            adapter = channelsListAdapter.apply {
                onClick = this@ChannelsFragment::goToOtherView
                onFollowClick = this@ChannelsFragment::updateFollowers
            }
            layoutManager = LinearLayoutManager(context)
        }
        presenter?.loadChannels {
            presenter.extendChannelList?.let {
                channelsListAdapter.data = it
            }
        }
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh.setOnRefreshListener {
            presenter?.loadChannelsFromRemote(
                    { _, _ ->
                        swipe_refresh.isRefreshing = false
                    },
                    {
                        presenter.extendChannelList?.let {
                            channelsListAdapter.data = it
                        }
                        swipe_refresh.isRefreshing = false
                    }
            )
        }
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