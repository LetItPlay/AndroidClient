package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.channels_item.view.*

class ChannelAdapter(
        private val onClick: (Channel) -> Unit,
        private val onFollowClick: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    private var channels: List<Channel> = emptyList()

    fun updateChannels(channels: List<Channel>) {
        val diffResult = DiffUtil.calculateDiff(ChannelDiffer(this.channels, channels))
        this.channels = channels
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.update(channels[position])
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        if (FOLLOW_CHANGED in payloads) {
            holder.updateFollow(channels[position])
        }
    }

    override fun getItemCount(): Int = channels.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        return ChannelViewHolder(parent, onClick, onFollowClick)
    }

    inner class ChannelViewHolder(
            val parent: ViewGroup?,
            onClick: (Channel) -> Unit,
            onFollowClick: (Channel) -> Unit
    ) : BaseViewHolder(parent, R.layout.channels_item) {
        lateinit var channelData: Channel

        init {
            itemView.tag_container.removeNotFullVisible = true
            itemView.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onClick(channelData)
                }
            }
            itemView.channel_follow.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onFollowClick(channelData)
                }
            }
        }

        fun update(channelData: Channel) {
            this.channelData = channelData
            itemView.apply {
                with(channelData) {
                    channel_title.text = name
                    channel_logo.loadImage(imageUrl, placeholder = R.drawable.channel_placeholder)
                    tag_container.setTagList(tags ?: emptyList())
                }
            }
            updateFollow(channelData)
        }

        fun updateFollow(channelData: Channel) {
            this.channelData = channelData
            itemView.apply {
                channel_follow.isFollowing = channelData.followed ?: false
                follower_count.text = channelData.subscriptionCount.toString()
            }
        }

    }

    companion object {
        val FOLLOW_CHANGED = Any()
    }
}