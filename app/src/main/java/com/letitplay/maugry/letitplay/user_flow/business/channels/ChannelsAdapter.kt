package com.letitplay.maugry.letitplay.user_flow.business.channels

import android.support.v7.widget.RecyclerView
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
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClick?.invoke(data[position].channel?.id) }
            itemView.channel_follow.setOnClickListener { onFollowClick?.invoke(data[position], it.channel_follow.isFollow(), position) }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelsItemHolder = ChannelsItemHolder(parent)

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