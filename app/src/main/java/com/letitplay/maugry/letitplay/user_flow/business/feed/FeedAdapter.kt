package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import kotlinx.android.synthetic.main.feed_item.view.*
import java.util.*

class FeedAdapter(
        private val musicService: MusicService? = null,
        private val onClickItem: ((Long) -> Unit)? = null,
        private val onLikeClick: ((ExtendTrackModel, Boolean, Int) -> Unit)? = null,
        private val playlistActionsListener: OnPlaylistActionsListener? = null
) : RecyclerView.Adapter<FeedItemViewHolder>() {

    var data: List<ExtendTrackModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: FeedItemViewHolder?, position: Int) {
        holder?.update(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedItemViewHolder {
        return FeedItemViewHolder(parent, playlistActionsListener).apply {
            itemView.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onClickItem?.invoke(data[adapterPosition].track?.id!!)
                }
            }
            itemView.feed_like.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    it.isEnabled = false
                    onLikeClick?.invoke(data[adapterPosition], it.feed_like.isLiked(), adapterPosition)
                }
            }

            itemView.feed_playing_now.mediaSession = musicService?.mediaSession
        }
    }
}