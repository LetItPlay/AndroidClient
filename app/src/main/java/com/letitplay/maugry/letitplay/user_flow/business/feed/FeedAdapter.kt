package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import kotlinx.android.synthetic.main.feed_item.view.*

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedChannelsItemHolder>() {

    var data: List<Pair<ChannelModel, TrackModel>> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClickItem: ((Long) -> Unit)? = null


    override fun onBindViewHolder(holder: FeedChannelsItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClickItem?.invoke(data[position].second.id!!) }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedChannelsItemHolder = FeedChannelsItemHolder(parent)

    class FeedChannelsItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.feed_item) {

        fun update(pair: Pair<ChannelModel, TrackModel>) {
            itemView.apply {
                feed_like.text = pair.second.like_count.toString()
                feed_time.text = pair.second.audio_file?.length_seconds.toString()
                feed_track_title.text = pair.second.name
                feed_channel_title.text = pair.first.name
                Glide.with(context)
                        .load("$GL_MEDIA_SERVICE_URL${pair.first.image}")
                        .into(feed_channel_logo)
                Glide.with(context)
                        .load("$GL_MEDIA_SERVICE_URL${pair.second.image}")
                        .into(feed_track_image)

            }
        }
    }
}