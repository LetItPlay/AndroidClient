package com.letitplay.maugry.letitplay.user_flow.business.channels

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelItemModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.utils.loadImage
import com.letitplay.maugry.letitplay.utils.splitTags
import kotlinx.android.synthetic.main.channels_item.view.*


class ChannelAdapter : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {
    var data: List<ChannelItemModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClick: ((Int?) -> Unit)? = null
    var onFollowClick: ((ChannelItemModel, Boolean, Int) -> Unit)? = null

    override fun onBindViewHolder(holder: ChannelViewHolder?, position: Int) {
        holder?.update(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelViewHolder {
        return ChannelViewHolder(parent).apply {
            itemView.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onClick?.invoke(data[adapterPosition].channel?.id)
                }
            }
            itemView.channel_follow.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onFollowClick?.invoke(data[adapterPosition], it.channel_follow.isFollow(), adapterPosition)
                }
            }
        }
    }

    class ChannelViewHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channels_item) {

        fun update(channelModel: ChannelItemModel) {
            itemView.apply {
                channel_follow.data = channelModel.following
                channel_title.text = channelModel.channel?.name
                follower_count.text = channelModel.channel?.subscriptionCount.toString()
                channel_logo.loadImage(channelModel.channel?.imageUrl)
                val tags = channelModel.channel?.tags?.splitTags()
                if (tags != null)
                    tag_container.setTagList(tags)
            }
        }

    }
}