package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.channel_page_item.view.*


class ChannelPageAdapter(
        private val onClickItem: ((Int) -> Unit)
) : RecyclerView.Adapter<ChannelPageAdapter.ChannelPageItemHolder>() {

    private var data: List<Track> = ArrayList()

    override fun onBindViewHolder(holder: ChannelPageItemHolder, position: Int) {
        holder.update(data[position])
    }

    fun setData(channelList: List<Track>?) {
        channelList?.let {
            data = it
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChannelPageItemHolder {
        return ChannelPageItemHolder(parent).apply {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClickItem(data[adapterPosition].id)
                }
            }
        }
    }

    class ChannelPageItemHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channel_page_item) {

        fun update(extendTrack: Track) {
            itemView.apply {
                channel_page_playing_now.text = extendTrack.listenCount?.toString() ?: "0"
                channel_page_track_title.text = extendTrack.title
                channel_page_last_update.text = DateHelper.getShortPastDate(extendTrack.publishedAt, context)
                channel_page_track_preview.loadImage(extendTrack.coverUrl)
            }
        }

    }
}