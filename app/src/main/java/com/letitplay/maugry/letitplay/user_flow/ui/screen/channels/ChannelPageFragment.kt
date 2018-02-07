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
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateFollowersRequestBody
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPageAdapter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPagePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.loadCircularImage
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import kotlinx.android.synthetic.main.channel_page_fragment.*

class ChannelPageFragment : BaseFragment<ChannelPagePresenter>(R.layout.channel_page_fragment, ChannelPagePresenter) {

    private lateinit var recentAddedListAdapter: ChannelPageAdapter
    private var channelPageRepo: MusicRepo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val recentAddedRecycler = view.findViewById<RecyclerView>(R.id.recent_added_list)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val listDivider = listDivider(recentAddedRecycler.context, R.drawable.list_transparent_divider_16dp, layoutManager.orientation)
        recentAddedListAdapter = ChannelPageAdapter(::playTrack)
        recentAddedRecycler.layoutManager = layoutManager
        recentAddedRecycler.adapter = recentAddedListAdapter
        recentAddedRecycler.addItemDecoration(listDivider)
        return view
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
                    channel_page_preview.loadCircularImage(imageUrl)
                    channel_page_title.text = name
                    channel_page_followers.text = subscriptionCount.toString()

                    channel_page_follow.data = following
                    channel_page_follow.setOnClickListener {
                        channel_page_follow.isEnabled = false
                        updateFollowers(channelInfo, channel_page_follow.isFollow())
                    }

                    val tags = tags
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

        val followerModel: UpdateFollowersRequestBody = if (isFollow) UpdateFollowersRequestBody.buildUnFollowRequest()
        else UpdateFollowersRequestBody.buildFollowRequest()

        extendedChannel?.channel?.id?.let {
            presenter?.updateChannelFollowers(extendedChannel, followerModel) {
                presenter.updatedChannel?.let {
                    channel_page_follow.data = extendedChannel.following
                    channel_page_followers.text = extendedChannel.channel?.subscriptionCount.toString()
                    channel_page_follow.isEnabled = true
                }
            }
        }
    }

    private fun playTrack(trackId: Long) {
        if (channelPageRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
        // FIXME: pass new repository as argument
        channelPageRepo = MusicRepo(presenter!!.recentTracks.map {
            (it.channel to it.track).toAudioTrack()
        })
        navigationActivity.updateRepo(trackId, channelPageRepo)
    }

}