package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.model.FeedData
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
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var tracks: List<FeedData> = emptyList()
    var channels: List<Channel> = emptyList()

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) CHANNELS_TYPE else TRACK_TYPE
    }

    fun updateData(tracks: List<FeedData>, channels: List<Channel>) {
        this.tracks = tracks
        this.channels = channels
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = tracks.size+1

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHANNELS_TYPE -> ChannelsListViewHolder(parent, onChannelClick, seeAllChannelClick)
            TRACK_TYPE -> FeedItemViewHolder(
                    parent,
                    playlistActionsListener,
                    onClickItem,
                    onLikeClick,
                    musicService
            )
            else -> throw IllegalArgumentException("")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FeedItemViewHolder -> holder.update(tracks[position-1])
            is ChannelsListViewHolder -> holder.update(channels)
        }
    }

    companion object {
        const val CHANNELS_TYPE = 0
        const val TRACK_TYPE = 1
    }
}