package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.widget.LikeWidget
import com.letitplay.maugry.letitplay.utils.loadImage
import kotlinx.android.synthetic.main.feed_item.view.*
import java.util.*

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedItemViewHolder>() {

    var data: List<ExtendTrackModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var musicService: MusicService? = null
    var onClickItem: ((Long) -> Unit)? = null
    var onLikeClick: ((ExtendTrackModel,Boolean, Int) -> Unit)? = null

    override fun onBindViewHolder(holder: FeedItemViewHolder?, position: Int) {
        holder?.update(data[position])
    }


    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedItemViewHolder {
        return FeedItemViewHolder(parent).apply {
            itemView.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onClickItem?.invoke(data[adapterPosition].track?.id!!)
                }
            }
            itemView.feed_like.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onLikeClick?.invoke(data[adapterPosition], it.feed_like.isLiked(), adapterPosition)
                }
            }
            itemView.feed_playing_now.mediaSession = musicService?.mediaSession
        }
    }


    class FeedItemViewHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.feed_item) {

        fun update(extendTrackModel: ExtendTrackModel) {
            itemView.apply {
                val data = DateHelper.getLongPastDate(extendTrackModel.track?.publishedAt, context)
                feed_like.like = extendTrackModel.like
                feed_playing_now.trackListenerCount = extendTrackModel.track?.listenCount
                feed_playing_now.trackUrl = "$GL_MEDIA_SERVICE_URL${extendTrackModel.track?.audio?.fileUrl}"
                feed_time.text = DateHelper.getTime(extendTrackModel.track?.audio?.lengthInSeconds)
                feed_track_title.text = extendTrackModel.track?.name
                feed_channel_title.text = extendTrackModel.channel?.name
                feed_track_last_update.text = data
                feed_channel_logo.loadImage( extendTrackModel.channel?.imageUrl)
                feed_track_image.loadImage( extendTrackModel.track?.image)
            }
        }
    }
}