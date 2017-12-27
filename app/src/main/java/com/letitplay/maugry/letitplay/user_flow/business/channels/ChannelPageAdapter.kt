package com.letitplay.maugry.letitplay.user_flow.business.channels

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.letitplay.maugry.letitplay.GL_IMAGE_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import kotlinx.android.synthetic.main.channel_page_item.view.*
import kotlinx.android.synthetic.main.channels_item.view.*


class ChannelPageAdapter : RecyclerView.Adapter<ChannelPageAdapter.ChannelPageItemHolder>() {

    private var data: List<TrackModel> = ArrayList()
    var onClick: (() -> Unit)? = null

    override fun onBindViewHolder(holder: ChannelPageAdapter.ChannelPageItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClick?.invoke() }
        }
    }

    fun setData(channelList: List<TrackModel>?) {
        channelList?.let {
            data = it
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelPageItemHolder = ChannelPageAdapter.ChannelPageItemHolder(parent)


    class ChannelPageItemHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channel_page_item) {

        fun update(track: TrackModel) {

            itemView.apply {
                channel_page_track_title.text = track.name
                Glide.with(context)
                        .load("${GL_IMAGE_SERVICE_URL}${track.image}")
                        .into(channel_page_track_preview)
            }

        }

    }
}