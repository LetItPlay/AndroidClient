package com.letitplay.maugry.letitplay.user_flow.business.channels

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.utils.loadImage
import kotlinx.android.synthetic.main.channels_item.view.*


class ChannelsAdapter : RecyclerView.Adapter<ChannelsAdapter.ChannelsItemHolder>() {
    var data: List<ChannelModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClick: ((Int?) -> Unit)? = null
    var onFollowClick: ((Int?, Boolean) -> Unit)? = null

    override fun onBindViewHolder(holder: ChannelsItemHolder?, position: Int) {
        holder?.apply {
            var followerModel: FollowingChannelModel? = FollowingChannelModel().query { it.equalTo("id", data[position].id) }.firstOrNull()
            if (followerModel == null) {
                followerModel = FollowingChannelModel(data[position].id, false)
                followerModel.save()
            }
            update(data[position], followerModel)
            itemView.setOnClickListener { onClick?.invoke(data[position].id) }
            itemView.channel_follow.setOnClickListener { onFollowClick?.invoke(data[position].id, it.channel_follow.isFollow()) }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelsItemHolder = ChannelsItemHolder(parent)

    class ChannelsItemHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channels_item) {

        fun update(channel: ChannelModel, followerModel: FollowingChannelModel) {
            itemView.apply {
                channel_follow.data = followerModel
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