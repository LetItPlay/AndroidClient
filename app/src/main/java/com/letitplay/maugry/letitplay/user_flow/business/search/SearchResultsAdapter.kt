package com.letitplay.maugry.letitplay.user_flow.business.search

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.business.player.TrackAdapter
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.channels_item_small.view.*
import kotlinx.android.synthetic.main.track_item.view.track_playing_now

typealias ChannelVH = SearchResultsAdapter.ChannelSmallViewHolder
typealias TrackVH = TrackAdapter.TrackItemHolder

sealed class ResultItem {
    class ChannelItem(val channelItemModel: ExtendChannelModel) : ResultItem()
    class TrackItem(val track: com.gsfoxpro.musicservice.model.AudioTrack) : ResultItem()
}

class SearchResultsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: List<ResultItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onChannelClick: ((ChannelModel) -> Unit)? = null
    var onTrackClick: ((AudioTrack) -> Unit)? = null
    var onFollowClick: ((ExtendChannelModel, Boolean, Int) -> Unit)? = null
    var musicService: MusicService? = null

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is ResultItem.ChannelItem -> CHANNEL_ITEM_TYPE
            is ResultItem.TrackItem -> TRACK_ITEM_TYPE
        }
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHANNEL_ITEM_TYPE -> ChannelVH(parent).apply {
                itemView.setOnClickListener {
                    if (adapterPosition != NO_POSITION) {
                        onChannelClick?.invoke((data[adapterPosition] as ResultItem.ChannelItem).channelItemModel.channel!!)
                    }
                }
                itemView.channel_follow.setOnClickListener {
                    if (adapterPosition != NO_POSITION) {
                        onFollowClick?.invoke(
                                (data[adapterPosition] as ResultItem.ChannelItem).channelItemModel,
                                it.channel_follow.isFollow(),
                                adapterPosition
                        )
                    }
                }
            }
            TRACK_ITEM_TYPE -> TrackVH(parent).apply {
                itemView.setOnClickListener {
                    if (adapterPosition != NO_POSITION) {
                        onTrackClick?.invoke((data[adapterPosition] as ResultItem.TrackItem).track)
                    }
                }
                itemView.track_playing_now.mediaSession = musicService?.mediaSession
            }
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when {
            holder is ChannelVH && item is ResultItem.ChannelItem -> holder.update(item.channelItemModel)
            holder is TrackVH && item is ResultItem.TrackItem -> holder.update(item.track)
        }
    }

    class ChannelSmallViewHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channels_item_small) {

        fun update(channelItem: ExtendChannelModel) {
            itemView.apply {
                channel_name.text = channelItem.channel!!.name
                channel_follow.data = channelItem.following
                channel_followers_count.text = channelItem.channel!!.subscriptionCount.toString()
                channel_small_logo.loadImage(channelItem.channel!!.imageUrl)
            }
        }
    }

    companion object {
        const val CHANNEL_ITEM_TYPE = 1
        const val TRACK_ITEM_TYPE = 2
    }
}