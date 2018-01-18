package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowersModel
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPageAdapter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPagePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.utils.loadImage
import com.letitplay.maugry.letitplay.utils.splitTags
import kotlinx.android.synthetic.main.channel_page_fragment.*

class ChannelPageFragment : BaseFragment<ChannelPagePresenter>(R.layout.channel_page_fragment, ChannelPagePresenter) {

    private var recentAddedListAdapter = ChannelPageAdapter()
    private var channelPageRepo: MusicRepo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState).also {
            val recentAddedRecycler = it?.findViewById<RecyclerView>(R.id.recent_added_list)
            recentAddedRecycler?.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = recentAddedListAdapter
            }
            recentAddedListAdapter.onClickItem = this::playTrack
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id: Int = getKey()
        presenter?.loadTracks(id) {
            val channelInfo = presenter.extendChannel ?: return@loadTracks
            with(channelInfo) {
                val channel = channel ?: return@loadTracks
                with(channel) {
                    channel_page_banner.loadImage(imageUrl)
                    channel_page_preview.loadImage(imageUrl)
                    channel_page_title.text = name
                    channel_page_followers.text = subscriptionCount.toString()

                    channel_page_follow.data = following
                    channel_page_follow.setOnClickListener {
                        updateFollowers(channelInfo, channel_page_follow.isFollow())
                    }

                    val tags = tags?.splitTags()
                    if (tags != null)
                        channel_page_tag_container.setTagList(tags)
                }
            }
            if (presenter.recentTracks.isNotEmpty()) {
                recentAddedListAdapter.setData(presenter.recentTracks)
                channel_page_recent_added.visibility = View.VISIBLE
            }
        }


    }

    private fun updateFollowers(extendedChannel: ExtendChannelModel?, isFollow: Boolean) {

        val followerModel: FollowersModel = if (isFollow) FollowersModel(1)
        else FollowersModel(-1)

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
        navigationActivity.updateRepo(trackId, channelPageRepo)
    }

}