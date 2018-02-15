package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.channels_item.view.*

class ChannelAdapter(
        private val onClick: (Channel) -> Unit,
        private val onFollowClick: (ChannelWithFollow) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {
    var data: List<ChannelWithFollow> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ChannelViewHolder?, position: Int) {
        holder?.update(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelViewHolder {
        return ChannelViewHolder(parent, onClick, onFollowClick)
    }

    class ChannelViewHolder(val parent: ViewGroup?,
                            onClick: (Channel) -> Unit,
                            onFollowClick: (ChannelWithFollow) -> Unit
    ) : BaseViewHolder(parent, R.layout.channels_item) {
        lateinit var channelData: ChannelWithFollow

        init {
            itemView.tag_container.removeNotFullVisible = true
            itemView.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onClick(channelData.channel)
                }
            }
            itemView.channel_follow.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    it.isEnabled = false
                    onFollowClick(channelData)
                }
            }
        }

        fun update(channelData: ChannelWithFollow) {
            this.channelData = channelData
            itemView.apply {
                channel_follow.isEnabled = true
                if (!channelData.isFollowing) {
                    channel_follow.setFollow()
                } else {
                    channel_follow.setUnfollow()
                }
                with(channelData.channel) {
                    channel_title.text = name
                    follower_count.text = subscriptionCount.toString()
                    channel_logo.loadImage(imageUrl)
                    tag_container.setTagList(tags ?: emptyList())
                }
            }
        }

    }
}