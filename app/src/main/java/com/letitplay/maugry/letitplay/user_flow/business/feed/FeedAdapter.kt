package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.FeedItemModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DataHelper
import kotlinx.android.synthetic.main.feed_item.view.*
import java.util.*

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedChannelsItemHolder>() {

    var data: List<FeedItemModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var musicService: MusicService? = null
    var onClickItem: ((Long) -> Unit)? = null
    var onLikeClick: ((FeedItemModel, Boolean, Int) -> Unit)? = null

    override fun onBindViewHolder(holder: FeedChannelsItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClickItem?.invoke(data[position].track?.id!!) }
            itemView.feed_like.setOnClickListener {
                onLikeClick?.invoke(data[position], it.feed_like.isLiked(), position)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedChannelsItemHolder {
        return FeedChannelsItemHolder(parent).apply {
            itemView.feed_playing_now.mediaSession = musicService?.mediaSession
        }
    }


    class FeedChannelsItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.feed_item) {

        fun update(feedItemModel: FeedItemModel) {
            itemView.apply {
                val data = DataHelper.getData(feedItemModel.track?.publishedAt!!, context)
                feed_like.like = feedItemModel.like
                feed_playing_now.trackListenerCount = feedItemModel.track?.listenCount
                feed_playing_now.trackUrl = "$GL_MEDIA_SERVICE_URL${feedItemModel.track?.audio?.fileUrl}"
                feed_time.text = DataHelper.getTime(feedItemModel.track?.audio?.lengthInSeconds)
                feed_track_title.text = feedItemModel.track?.name
                feed_channel_title.text = feedItemModel.track?.name
                feed_track_last_update.text = data
                Glide.with(context)
                        .load("$GL_MEDIA_SERVICE_URL${feedItemModel.channel?.imageUrl}")
                        .into(feed_channel_logo)
                Glide.with(context)
                        .load("$GL_MEDIA_SERVICE_URL${feedItemModel.track?.image}")
                        .into(feed_track_image)

            }
        }
    }
}