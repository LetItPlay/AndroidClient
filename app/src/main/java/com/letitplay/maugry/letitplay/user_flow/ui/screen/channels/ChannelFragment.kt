package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.defaultItemAnimator


class ChannelFragment : BaseFragment<ChannelPresenter>(R.layout.channels_fragment, ChannelPresenter) {

    private val channelsListAdapter: ChannelAdapter by lazy {
        ChannelAdapter(::gotoChannelPage, ::onFollowClick)
    }

    private val vm by lazy {
        ViewModelProviders.of(this, ServiceLocator.viewModelFactory)
                .get(ChannelViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.channels.observe(this, Observer<List<ChannelWithFollow>> {
            it?.let {
                channelsListAdapter.updateChannels(it)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val channelRecycler = view.findViewById<RecyclerView>(R.id.channels_list)
        channelRecycler.adapter = channelsListAdapter
        val listDivider = listDivider(channelRecycler.context, R.drawable.list_divider)
        channelRecycler.addItemDecoration(listDivider)
        channelRecycler.defaultItemAnimator.supportsChangeAnimations = false
        channelRecycler.setHasFixedSize(true)
//        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
//        swipeRefreshLayout.setOnRefreshListener {
//            presenter?.loadChannels (
//                    false,
//                    { _, _ ->
//                        swipeRefreshLayout.isRefreshing = false
//                    },
//                    {
//                        presenter.extendChannelList?.let {
//                            channelsListAdapter.data = it
//                        }
//                        swipeRefreshLayout.isRefreshing = false
//                    }
//            )
//        }
        return view
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        presenter?.loadChannels {
//            presenter.extendChannelList?.let {
//                channelsListAdapter.data = it
//            }
//        }
//    }
//
    private fun gotoChannelPage(channel: Channel) {
//        if (swipe_refresh.isRefreshing) return
//        id?.let {
//            ChannelPagePresenter.extendChannel = presenter?.extendChannelList?.first { it.id == id }
//            navigationActivity.navigateTo(ChannelPageKey(id))
//        }
    }

    private fun onFollowClick(channel: ChannelWithFollow) {
        vm.onFollowClick(channel)
//        if (swipe_refresh.isRefreshing) return
//        val followerModel: UpdateFollowersRequestBody = if (isFollow) UpdateFollowersRequestBody.UNFOLLOW()
//        else UpdateFollowersRequestBody.buildFollowRequest()
//
//        extendChannel.channel?.id?.let {
//            presenter?.updateChannelFollowers(extendChannel, followerModel) {
//                presenter.updatedChannel?.let {
//                    channelsListAdapter.notifyItemChanged(position)
//                }
//            }
//        }
    }

}