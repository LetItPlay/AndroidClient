package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelAdapter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPagePresenter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.defaultItemAnimator
import kotlinx.android.synthetic.main.channels_fragment.*


class ChannelsFragment : BaseFragment<ChannelPresenter>(R.layout.channels_fragment, ChannelPresenter) {

    private lateinit var channelsListAdapter: ChannelAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val channelRecycler = view.findViewById<RecyclerView>(R.id.channels_list)
        channelsListAdapter = ChannelAdapter(::gotoChannelPage, ::updateFollowers)
        val layoutManager = LinearLayoutManager(context)
        channelRecycler.adapter = channelsListAdapter
        channelRecycler.layoutManager = layoutManager
        val listDivider = listDivider(channelRecycler.context, R.drawable.list_divider)
        channelRecycler.addItemDecoration(listDivider)
        channelRecycler.defaultItemAnimator.supportsChangeAnimations = false
        channelRecycler.setHasFixedSize(true)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            presenter?.loadChannelsFromRemote(
                    { _, _ ->
                        swipeRefreshLayout.isRefreshing = false
                    },
                    {
                        presenter.extendChannelList?.let {
                            channelsListAdapter.data = it
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
            )
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.loadChannels {
            presenter.extendChannelList?.let {
                channelsListAdapter.data = it
            }
        }
    }

    private fun gotoChannelPage(id: Int?) {
        if (swipe_refresh.isRefreshing) return
        id?.let {
            ChannelPagePresenter.extendChannel = presenter?.extendChannelList?.first { it.id == id }
            navigationActivity.navigateTo(ChannelPageKey(id))
        }
    }

    private fun updateFollowers(extendChannel: ExtendChannelModel, isFollow: Boolean, position: Int) {
        if (swipe_refresh.isRefreshing) return
        val followerModel: UpdateFollowersRequestBody = if (isFollow) UpdateFollowersRequestBody.buildUnFollowRequest()
        else UpdateFollowersRequestBody.buildFollowRequest()

        extendChannel.channel?.id?.let {
            presenter?.updateChannelFollowers(extendChannel, followerModel) {
                presenter.updatedChannel?.let {
                    channelsListAdapter.notifyItemChanged(position)
                }
            }
        }
    }

}