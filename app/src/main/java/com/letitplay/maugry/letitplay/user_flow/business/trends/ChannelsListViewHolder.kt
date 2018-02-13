package com.letitplay.maugry.letitplay.user_flow.business.trends

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.Channel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.loadCircularImage
import kotlinx.android.synthetic.main.channel_list_item.view.*
import kotlinx.android.synthetic.main.channel_small_item.view.*


class ChannelsListViewHolder(
        parent: ViewGroup?,
        private val onChannelClick: ((Channel) -> Unit),
        seeAllClick: (() -> Unit)
) : BaseViewHolder(parent, R.layout.channel_list_item) {

    init {
        itemView.channelsRecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            layoutManager = linearLayoutManager
            adapter = ChannelsAdapter()
            addItemDecoration(listDivider(itemView.context, R.drawable.list_transparent_divider_16dp, linearLayoutManager.orientation))
        }
        itemView.allChannelsText.setOnClickListener {
            seeAllClick()
        }
    }

    fun update(channels: List<Channel>) {
        (itemView.channelsRecyclerView.adapter as ChannelsAdapter).channels = channels
    }

    inner class ChannelsAdapter : RecyclerView.Adapter<ChannelsAdapter.ChannelsViewHolder>() {

        var channels: List<Channel> = emptyList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelsViewHolder {
            return ChannelsViewHolder(parent).apply {
                itemView.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onChannelClick(channels[adapterPosition])
                    }
                }
            }
        }

        override fun getItemCount(): Int = channels.size

        override fun onBindViewHolder(holder: ChannelsViewHolder, position: Int) {
            holder.update(channels[position])
        }

        inner class ChannelsViewHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.channel_small_item) {
            fun update(channel: Channel) {
                itemView.channel_icon.loadCircularImage(channel.imageUrl)
            }
        }
    }
}