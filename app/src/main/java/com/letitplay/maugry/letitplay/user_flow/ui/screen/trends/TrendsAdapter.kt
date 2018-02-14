package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedItemViewHolder
import com.letitplay.maugry.letitplay.user_flow.business.feed.OnPlaylistActionsListener
import com.letitplay.maugry.letitplay.user_flow.business.trends.ChannelsListViewHolder


class TrendsAdapter(
        private val musicService: MusicService?,
        private val onClickItem: ((Track, Int) -> Unit),
        private val onLikeClick: ((Track, Boolean, Int) -> Unit),
        private val playlistActionsListener: OnPlaylistActionsListener? = null,
        private val onChannelClick: ((Channel) -> Unit),
        private val seeAllChannelClick: (() -> Unit)
) : PagedListAdapter<TrackWithChannel, RecyclerView.ViewHolder>(TRACK_WITH_CHANNEL_COMPARATOR) {

    var channels: List<Channel> = emptyList()

    fun updateChannels(channels: List<Channel>) {
        this.channels = channels
        notifyItemChanged(CHANNEL_ROW)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == CHANNEL_ROW) R.layout.channel_list_item else R.layout.feed_item
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (super.getItemCount() != 0) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.channel_list_item -> ChannelsListViewHolder(parent, onChannelClick, seeAllChannelClick)
            R.layout.feed_item -> FeedItemViewHolder(
                    parent,
                    playlistActionsListener,
                    onClickItem,
                    onLikeClick,
                    musicService
            )
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FeedItemViewHolder -> holder.update(getItem(position-1))
            is ChannelsListViewHolder -> holder.update(channels)
        }
    }

    companion object {
        const val CHANNEL_ROW = 0

        val TRACK_WITH_CHANNEL_COMPARATOR = object: DiffCallback<TrackWithChannel>() {
            override fun areItemsTheSame(oldItem: TrackWithChannel, newItem: TrackWithChannel) =
                oldItem.track.id == newItem.track.id


            override fun areContentsTheSame(oldItem: TrackWithChannel, newItem: TrackWithChannel) =
                    oldItem == newItem

        }
    }
}