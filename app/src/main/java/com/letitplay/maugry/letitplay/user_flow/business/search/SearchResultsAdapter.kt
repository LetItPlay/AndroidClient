package com.letitplay.maugry.letitplay.user_flow.business.search

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.business.player.TrackAdapter
import com.letitplay.maugry.letitplay.utils.loadImage
import kotlinx.android.synthetic.main.channels_item_small.view.*

typealias ChannelVH = SearchResultsAdapter.ChannelSmallViewHolder
typealias TrackVH = TrackAdapter.TrackItemHolder

sealed class ResultItem {
    class ChannelItem(val channel: ChannelModel) : ResultItem()
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
                        onChannelClick?.invoke((data[adapterPosition] as ResultItem.ChannelItem).channel)
                    }
                }
            }
            TRACK_ITEM_TYPE -> TrackVH(parent).apply {
                itemView.setOnClickListener {
                    if (adapterPosition != NO_POSITION) {
                        onTrackClick?.invoke((data[adapterPosition] as ResultItem.TrackItem).track)
                    }
                }
            }
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when {
            holder is ChannelVH && item is ResultItem.ChannelItem -> holder.update(item.channel)
            holder is TrackVH && item is ResultItem.TrackItem -> holder.update(item.track)
        }
    }

    companion object {
        const val CHANNEL_ITEM_TYPE = 1
        const val TRACK_ITEM_TYPE = 2
    }

    class ChannelSmallViewHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channels_item_small) {

        fun update(channel: ChannelModel) {
            itemView.apply {
                channel_name.text = channel.name
                channel_followers_count.text = channel.subscriptionCount.toString()
                channel_small_logo.loadImage(context, channel.imageUrl)
            }
        }
    }
}