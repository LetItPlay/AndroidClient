package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toAudioTrack
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.SharedHelper
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.loadCircularImage
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.channel_page_fragment.*

class ChannelPageFragment : BaseFragment(R.layout.channel_page_fragment) {

    private val recentAddedListAdapter = ChannelPageAdapter(::onTrackClicked)
    private var channelPageRepo: MusicRepo? = null
    private var channelPageData: ChannelWithFollow? = null

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(ChannelPageViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.channelId.value = getKey()
        vm.channelWithFollow.observe(this, Observer<ChannelWithFollow> {
            it?.let { channelData ->
                with(channelData.channel) {
                    channelPageData = it
                    channel_page_banner.loadImage(imageUrl, placeholder = R.drawable.channel_banner)
                    channel_page_preview.loadCircularImage(imageUrl)
                    channel_page_title.text = name
                    if (!channelDescription.isNullOrEmpty()) {
                        channel_page_description.text = channelDescription
                        channel_page_description.visibility = View.VISIBLE
                    }
                    channel_page_followers.text = subscriptionCount.toString()

                    val tags = tags
                    if (tags != null)
                        channel_page_tag_container.setTagList(tags)
                }
                channel_page_follow.isEnabled = true
                channel_page_follow.isFollowing = it.isFollowing
                channel_page_share?.let {
                    it.setOnClickListener {
                        SharedHelper.channelShare(it.context, channelData.channel.name, channelData.channel.id)
                    }
                }
            }
        })
        vm.recentAddedChannelTracks.observe(this, Observer<List<Track>> {
            it?.let {
                recentAddedListAdapter.setData(it)
                channel_page_recent_added.visibility = View.VISIBLE
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val recentAddedRecycler = view.findViewById<RecyclerView>(R.id.recent_added_list)
        val listDivider = listDivider(recentAddedRecycler.context, R.drawable.list_transparent_divider_16dp, LinearLayoutManager.HORIZONTAL)
        recentAddedRecycler.adapter = recentAddedListAdapter
        recentAddedRecycler.addItemDecoration(listDivider)
        val followButton = view.findViewById<View>(R.id.channel_page_follow)
        followButton.setOnClickListener {
            channel_page_follow.isEnabled = false
            vm.onFollowClick()
        }
        return view
    }

    private fun onTrackClicked(track: Track) {
        if (channelPageRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(track.id)
            return
        }
        val channel = vm.channelWithFollow.value
        val tracks = vm.recentAddedChannelTracks.value
        if (channel != null && tracks != null) {
            val tracksList = tracks.map {
                TrackWithChannel(it, channel.channel, null)
            }
            channelPageRepo = MusicRepo(tracksList.map(TrackWithChannel::toAudioTrack).toMutableList())
            navigationActivity.updateRepo(track.id, channelPageRepo, tracksList)
        }
    }

}