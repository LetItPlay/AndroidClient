package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelAdapter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPagePresenter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.channels_fragment.*


class ChannelsFragment : BaseFragment<ChannelPresenter>(R.layout.channels_fragment, ChannelPresenter) {

    private var channelsListAdapter = ChannelAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState).also {
            val recycler = it?.findViewById<RecyclerView>(R.id.channels_list)
            recycler?.apply {
                adapter = channelsListAdapter.apply {
                    onClick = this@ChannelsFragment::gotoChannelPage
                    onFollowClick = this@ChannelsFragment::updateFollowers
                }
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channels_list.setHasFixedSize(true)
        presenter?.loadChannels({
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

    private fun gotoChannelPage(id: Int?) {
        id?.let {
            ChannelPagePresenter.extendChannel = presenter?.extendChannelList?.first { it.id == id }
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