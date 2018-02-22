package com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.data_management.model.SearchResultItem
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.TrackAdapter
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import kotlinx.android.synthetic.main.channels_item_small.view.*

typealias ChannelVH = SearchResultsAdapter.ChannelSmallViewHolder
typealias TrackVH = TrackAdapter.TrackItemHolder

class SearchResultsAdapter(
        private val musicService: MusicService?,
        private val onChannelClick: ((Channel) -> Unit),
        private val onTrackClick: (AudioTrack) -> Unit,
        private val onFollowClick: (ChannelWithFollow) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: List<SearchResultItem> = emptyList()

    fun updateItems(items: List<SearchResultItem>) {
        val diffResult = DiffUtil.calculateDiff(ResultsDiffer(this.data, items))
        this.data = items
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is SearchResultItem.ChannelItem -> CHANNEL_ITEM_TYPE
            is SearchResultItem.TrackItem -> TRACK_ITEM_TYPE
        }
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHANNEL_ITEM_TYPE -> ChannelVH(parent, onChannelClick, onFollowClick)
            TRACK_ITEM_TYPE -> TrackVH(parent, onTrackClick, musicService)
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        val item = data[position]
        if (FOLLOW_CHANGED in payloads && item is SearchResultItem.ChannelItem) {
            (holder as? ChannelVH)?.updateFollow(item.channel)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when {
            holder is ChannelVH && item is SearchResultItem.ChannelItem -> holder.update(item.channel)
            holder is TrackVH && item is SearchResultItem.TrackItem -> holder.update(item.track.toAudioTrack())
        }
    }

    class ChannelSmallViewHolder(
            parent: ViewGroup?,
            onClick: (Channel) -> Unit,
            onFollowClick: (ChannelWithFollow) -> Unit
    ) : BaseViewHolder(parent, R.layout.channels_item_small) {

        lateinit var channel: ChannelWithFollow

        init {
            itemView.setOnClickListener {
                onClick(channel.channel)
            }
            itemView.channel_follow.setOnClickListener {
                onFollowClick(channel)
            }
        }

        fun update(channelItem: ChannelWithFollow) {
            this.channel = channelItem
            itemView.apply {
                channel_name.text = channelItem.channel.name
                channel_small_logo.loadImage(channelItem.channel.imageUrl)
            }
            updateFollow(channelItem)
        }

        fun updateFollow(channelItem: ChannelWithFollow) {
            this.channel = channelItem
            itemView.apply {
                channel_follow.isFollowing = channelItem.isFollowing
                channel_followers_count.text = channelItem.channel.subscriptionCount.toString()
            }
        }
    }

    companion object {
        const val CHANNEL_ITEM_TYPE = 1
        const val TRACK_ITEM_TYPE = 2

        val FOLLOW_CHANGED = Any()
    }
}