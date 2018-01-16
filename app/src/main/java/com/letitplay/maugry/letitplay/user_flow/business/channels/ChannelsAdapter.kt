package com.letitplay.maugry.letitplay.user_flow.business.channels

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelItemModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.utils.loadImage
import kotlinx.android.synthetic.main.channels_item.view.*


class ChannelsAdapter : RecyclerView.Adapter<ChannelsAdapter.ChannelsItemHolder>() {
    var data: List<ChannelItemModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClick: ((Int?) -> Unit)? = null
    var onFollowClick: ((ChannelItemModel, Boolean, Int) -> Unit)? = null

    override fun onBindViewHolder(holder: ChannelsItemHolder?, position: Int) {
        holder?.update(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelsItemHolder {
        return ChannelsItemHolder(parent).apply {
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

    class ChannelsItemHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channels_item) {

        fun update(channelModel: ChannelItemModel) {
            itemView.apply {
                channel_follow.data = channelModel.following
                channel_title.text = channelModel.channel?.name
                follower_count.text = channelModel.channel?.subscriptionCount.toString()
                channel_logo.loadImage(context, channelModel.channel?.imageUrl)
                val tags = channelModel.channel?.tags?.split(",")?.map(String::trim)?.filter(String::isNotEmpty)
                if (tags != null)
                    tag_container.setTagList(tags)
            }
        }

    }
}