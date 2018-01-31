package com.letitplay.maugry.letitplay.user_flow.business.channels

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import com.letitplay.maugry.letitplay.utils.ext.splitTags
import kotlinx.android.synthetic.main.channels_item.view.*


class ChannelAdapter(
        private val onClick: ((Int?) -> Unit),
        private val onFollowClick: ((ExtendChannelModel, Boolean, Int) -> Unit)
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {
    var data: List<ExtendChannelModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ChannelViewHolder?, position: Int) {
        holder?.update(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelViewHolder {
        return ChannelViewHolder(parent).apply {
            itemView.tag_container.removeNotFullVisible = true
            itemView.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onClick(data[adapterPosition].channel?.id)
                }
            }
            itemView.channel_follow.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    it.isEnabled = false
                    onFollowClick(data[adapterPosition], it.channel_follow.isFollow(), adapterPosition)
                }
            }
        }
    }

    class ChannelViewHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channels_item) {

        fun update(extendChannelModel: ExtendChannelModel) {
            itemView.apply {
                channel_follow.isEnabled = true
                channel_follow.data = extendChannelModel.following
                channel_title.text = extendChannelModel.channel?.name
                follower_count.text = extendChannelModel.channel?.subscriptionCount.toString()
                channel_logo.loadImage(extendChannelModel.channel?.imageUrl)
                val tags = extendChannelModel.channel?.tags?.splitTags()
                tag_container.setTagList(tags ?: emptyList())
            }
        }

    }
}