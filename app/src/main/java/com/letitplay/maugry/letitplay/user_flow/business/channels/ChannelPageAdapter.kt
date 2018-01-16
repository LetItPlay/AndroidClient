package com.letitplay.maugry.letitplay.user_flow.business.channels

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.utils.loadImage
import kotlinx.android.synthetic.main.channel_page_item.view.*


class ChannelPageAdapter : RecyclerView.Adapter<ChannelPageAdapter.ChannelPageItemHolder>() {

    private var data: List<TrackModel> = ArrayList()
    var onClick: (() -> Unit)? = null

    override fun onBindViewHolder(holder: ChannelPageAdapter.ChannelPageItemHolder, position: Int) {
        holder.update(data[position])
    }

    fun setData(channelList: List<TrackModel>?) {
        channelList?.let {
            data = it
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) : ChannelPageItemHolder {
        return ChannelPageItemHolder(parent).apply {
            itemView.setOnClickListener { onClick?.invoke() }
        }
    }

    class ChannelPageItemHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channel_page_item) {

        fun update(track: TrackModel) {
            itemView.apply {
                channel_page_track_title.text = track.name
                channel_page_track_preview.loadImage(context, track.image)
            }
        }

    }
}