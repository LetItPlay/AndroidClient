package com.letitplay.maugry.letitplay.user_flow.business.channels

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder


class ChannelProfileAdapter : RecyclerView.Adapter<ChannelProfileAdapter.ChannelsProfileItemHolder>() {

    private var data: List<ChannelModel> = ArrayList()

    var onClick: (() -> Unit)? = null

    override fun onBindViewHolder(holder: ChannelsProfileItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClick?.invoke() }
        }
    }

    fun setData(channelList: List<ChannelModel>) {
        channelList.let {
            data = it
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelsProfileItemHolder = ChannelsProfileItemHolder(parent)

    class ChannelsProfileItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channel_profile_item) {

        fun update(channel: ChannelModel) {

        }

    }
}