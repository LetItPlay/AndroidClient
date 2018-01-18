package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPageAdapter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPagePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import kotlinx.android.synthetic.main.channel_page_fragment.*

class ChannelPageFragment : BaseFragment<ChannelPagePresenter>(R.layout.channel_page_fragment, ChannelPagePresenter) {

    private var recentAddedListAdapter = ChannelPageAdapter()
    private var mostListedListAdapter = ChannelPageAdapter()
    private var withGuestsListAdapter = ChannelPageAdapter()
    private var channelPageRepo: MusicRepo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id: Int = getKey()
        presenter?.loadTracks(id) {
            Glide.with(context)
                    .load("${GL_MEDIA_SERVICE_URL}${presenter.extendChannel?.channel?.imageUrl}")
                    .into(channel_page_banner)

            Glide.with(context)
                    .load("${GL_MEDIA_SERVICE_URL}${presenter.extendChannel?.channel?.imageUrl}")
                    .into(channel_page_preview)
            val tags = presenter.extendChannel?.channel?.tags?.split(",")

            channel_page_follow.data = presenter.extendChannel?.following
            channel_page_follow.setOnClickListener {
                updateFollowers(presenter.extendChannel, channel_page_follow.isFollow())
            }

            tags?.forEach {
                val view: TextView = LayoutInflater.from(context).inflate(R.layout.channel_tag, channel_page_teg_container, false) as TextView
                view.text = it
                channel_page_teg_container.addView(view)
            }
            channel_page_followers.text = presenter.extendChannel?.channel?.subscriptionCount.toString()

            channel_page_title.text = presenter.extendChannel?.channel?.name

            recent_added_list.apply {
                adapter = recentAddedListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            recentAddedListAdapter.onClickItem = { playTrack(it) }
            recentAddedListAdapter.musicService = musicService
            recentAddedListAdapter.setData(presenter.extendTrackList?.sortedBy { it.track?.listenCount })

            most_listened_list.apply {
                adapter = mostListedListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            mostListedListAdapter.onClickItem = { playTrack(it) }
            mostListedListAdapter.musicService = musicService
            mostListedListAdapter.setData(presenter.extendTrackList?.sortedBy { it.track?.listenCount })

            with_guests_list.apply {
                adapter = withGuestsListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            withGuestsListAdapter.onClickItem = { playTrack(it) }
            withGuestsListAdapter.musicService = musicService
            withGuestsListAdapter.setData(presenter.extendTrackList?.sortedBy { it.track?.listenCount })
        }


    }

    private fun updateFollowers(extendedChannel: ExtendChannelModel?, isFollow: Boolean) {

        var followerModel: FollowersModel
        if (isFollow) followerModel = FollowersModel(1)
        else followerModel = FollowersModel(-1)

        extendedChannel?.channel?.id?.let {
            presenter?.updateChannelFollowers(extendedChannel, followerModel) {
                presenter.updatedChannel?.let {
                    channel_page_follow.data = extendedChannel.following
                }
            }
        }
    }

    private fun playTrack(trackId: Long) {
        if (channelPageRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
        presenter?.playlist?.let {
            channelPageRepo = MusicRepo(it)
        }
        (activity as NavigationActivity).updateRepo(trackId, channelPageRepo)
    }

}