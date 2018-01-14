package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPageAdapter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPagePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.channel_page_fragment.*

class ChannelPageFragment : BaseFragment<ChannelPagePresenter>(R.layout.channel_page_fragment, ChannelPagePresenter) {

    private var recentAddedListAdapter = ChannelPageAdapter()
    private var mostListedListAdapter = ChannelPageAdapter()
    private var withGuestsListAdapter = ChannelPageAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id: Int = getKey()
        presenter?.loadTracks(id) {
            Glide.with(context)
                    .load("${GL_MEDIA_SERVICE_URL}${presenter.vmChannel?.imageUrl}")
                    .into(channel_page_banner)

            Glide.with(context)
                    .load("${GL_MEDIA_SERVICE_URL}${presenter.vmChannel?.imageUrl}")
                    .into(channel_page_preview)
            val tags = presenter.vmChannel?.tags?.split(",")

            channel_page_follow.data = FollowingChannelModel().query { it.equalTo("id", id) }.first()
            channel_page_follow.setOnClickListener {
                updateFollowers(id, channel_page_follow.isFollow())
            }

            tags?.forEach {
                val view: TextView = LayoutInflater.from(context).inflate(R.layout.channel_tag, channel_page_teg_container, false) as TextView
                view.text = it
                channel_page_teg_container.addView(view)
            }
            channel_page_followers.text = presenter.vmChannel?.subscriptionCount.toString()

            channel_page_title.text = presenter.vmChannel?.name
            recent_added_list.apply {
                adapter = recentAddedListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            recentAddedListAdapter.setData(presenter.vmTrackList?.sortedBy { it.listenCount })

            most_listened_list.apply {
                adapter = mostListedListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            mostListedListAdapter.setData(presenter.vmTrackList?.sortedBy { it.listenCount })
            with_guests_list.apply {
                adapter = withGuestsListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            withGuestsListAdapter.setData(presenter.vmTrackList?.sortedBy { it.listenCount })
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
                    channel_page_follow.data = channel
                }
            }
        }
    }

    private fun goToOtherView() {
    }

}