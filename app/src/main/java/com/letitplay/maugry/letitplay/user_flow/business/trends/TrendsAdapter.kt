package com.letitplay.maugry.letitplay.user_flow.business.trends

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedItemViewHolder
import com.letitplay.maugry.letitplay.user_flow.business.feed.OnPlaylistActionsListener


class TrendsAdapter(
        private val musicService: MusicService?,
        private val onClickItem: ((Long) -> Unit),
        private val onLikeClick: ((ExtendTrackModel, Boolean, Int) -> Unit),
        private val playlistActionsListener: OnPlaylistActionsListener? = null,
        private val onChannelClick: ((ChannelModel) -> Unit),
        private val seeAllChannelClick: (() -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var tracks: List<ExtendTrackModel> = emptyList()
    var channels: List<ChannelModel> = emptyList()

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) CHANNELS_TYPE else TRACK_TYPE
    }

    fun updateData(tracks: List<ExtendTrackModel>, channels: List<ChannelModel>) {
        this.tracks = tracks
        this.channels = channels
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = tracks.size

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