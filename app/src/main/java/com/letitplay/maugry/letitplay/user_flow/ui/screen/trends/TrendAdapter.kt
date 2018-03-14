package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.paging.HackyPagedListAdapterHelper
import android.arch.paging.PagedList
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedItemViewHolder
import com.letitplay.maugry.letitplay.user_flow.business.feed.OnPlaylistActionsListener
import com.letitplay.maugry.letitplay.user_flow.business.trends.ChannelsListViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedAdapter


class TrendAdapter(
        private val musicService: MusicService? = null,
        private val onClickItem: (TrackWithChannel) -> Unit,
        private val onLikeClick: (TrackWithChannel) -> Unit,
        private val playlistActionsListener: OnPlaylistActionsListener? = null,
        private val onChannelClick: (Channel) -> Unit,
        private val onSeeAllClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val helper = HackyPagedListAdapterHelper<TrackWithChannel>(this, FeedAdapter.TRACK_WITH_CHANNEL_COMPARATOR)

    var channels: List<Channel> = emptyList()
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    fun updateChannels(channels: List<Channel>) {
        this.channels = channels
        notifyItemChanged(0)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) R.layout.channel_list_item else R.layout.feed_item
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.channel_list_item -> ChannelsListViewHolder(parent, onChannelClick, onSeeAllClick)
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
            is FeedItemViewHolder -> holder.update(getItem(position - 1))
            is ChannelsListViewHolder -> holder.update(channels)
        }
    }

    override fun getItemCount(): Int = helper.itemCount + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            return super.onBindViewHolder(holder, position, payloads)
        }
        if (FeedAdapter.LIKE_CHANGED in payloads && holder is FeedItemViewHolder) {
            holder.updateLike(getItem(position - 1)!!)
        }
    }

    fun setList(pagedList: PagedList<TrackWithChannel>) {
        helper.setList(pagedList)
    }

    private fun getItem(position: Int): TrackWithChannel? = helper.getItem(position)

    fun getCurrentList(): PagedList<TrackWithChannel>? = helper.currentList
}