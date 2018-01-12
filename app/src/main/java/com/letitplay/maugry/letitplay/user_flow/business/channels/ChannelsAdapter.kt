package com.letitplay.maugry.letitplay.user_flow.business.channels

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.utils.loadImage
import kotlinx.android.synthetic.main.channels_item.view.*


class ChannelsAdapter : RecyclerView.Adapter<ChannelsAdapter.ChannelsItemHolder>() {

    private var data: List<ChannelModel> = ArrayList()
    var onClick: ((Int?) -> Unit)? = null
    var onFollowClick: ((Int?, view: TextView) -> Unit)? = null

    override fun onBindViewHolder(holder: ChannelsItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClick?.invoke(data[position].id) }
            itemView.channel_follow.setOnClickListener { onFollowClick?.invoke(data[position].id, itemView.channel_follow) }
        }
    }

    fun setData(channelList: List<ChannelModel>?) {
        channelList?.let {
            data = it
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelsItemHolder = ChannelsItemHolder(parent)

    class ChannelsItemHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channels_item) {

        fun update(channel: ChannelModel) {
            itemView.apply {
                channel_title.text = channel.name
                follower_count.text = channel.subscriptionCount.toString()
                channel_logo.loadImage(context, channel.imageUrl)
                val tags = channel.tags?.split(",")?.map(String::trim)?.filter(String::isNotEmpty)
                if (tags != null)
                    tag_container.setTagList(tags)
            }
        }

    }
}