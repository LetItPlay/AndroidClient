package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPresenter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelsAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
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
            channelsListAdapter.onFollowClick = this::updateFollowers
            channelsListAdapter.setData(presenter.channelList)
        })
    }

    private fun goToOtherView(id: Int?) {
        ChannelPageKey.param = id
        (activity as NavigationActivity).navigateTo(ChannelPageKey)
    }

    private fun changeState(state: String, view: TextView) {

        when (state) {
            "FOLLOW" -> {
                view.text = getString(R.string.channels_follow)
                view.setTextColor(ContextCompat.getColor(context,R.color.colorWhite))
                view.setBackgroundResource(R.drawable.follow_bg)
            }
            "FOLLOWING" -> {
                view.text = getString(R.string.channels_following)
                view.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
                view.setBackgroundResource(R.drawable.following_bg)
            }
        }

    }

    private fun getFollowersModel(id: Int?, view: TextView): FollowersModel {

        val model = presenter?.followingfChannelList?.firstOrNull { channel -> channel.id == id }
        var follower: FollowersModel

        if (model == null) {
            FollowingChannelModel(id, true).save()
            follower = FollowersModel(1)
            changeState("FOLLOW", view)
        } else {
            model.isFollowing = !model.isFollowing
            model.save()
            if (model.isFollowing) {
                follower = FollowersModel(1)
                changeState("FOLLOW", view)
            } else {
                follower = FollowersModel(-1)
                changeState("FOLLOWING", view)
            }
        }
        return follower
    }

    private fun updateFollowers(id: Int?, view: TextView) {

        var follower = getFollowersModel(id, view)

        id?.let {
            presenter?.updateChannelFollowers(id, follower) {
                if (presenter.channelModel != null) {
                    var index: Int? = presenter.channelList?.indexOfFirst { channel -> channel.id == id }
                    index?.let {
                        presenter.channelList?.get(index)?.subscription_count = presenter.channelModel?.subscription_count
                        channelsListAdapter.setData(presenter.channelList)
                    }
                }
            }
        }
    }

}