package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.playlist_header.view.*
import kotlinx.android.synthetic.main.track_item.view.*

class CurrentPlaylistAdapter(
        private val musicService: MusicService? = null,
        private val onClickItem: ((AudioTrack) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<AudioTrack> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TrackItemHolder -> holder.update(data[position - 1])
            is HeaderItemHolder -> {
                holder.trackCount.text = data.count().toString()
                holder.totalTime.text = DateHelper.getTime(data.sumBy { it.lengthInSeconds })
            }
        }
    }

    override fun getItemCount(): Int = data.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) R.layout.playlist_header else R.layout.playlist_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.playlist_item -> TrackItemHolder(parent, onClickItem, musicService)
            else -> HeaderItemHolder(parent)
        }
    }

    class HeaderItemHolder(
            parent: ViewGroup?
    ) : BaseViewHolder(parent, R.layout.playlist_header) {
        val totalTime: TextView get() = itemView.playlist_time
        val trackCount: TextView get() = itemView.playlist_count

        init {
            itemView.playlist_clear_all.visibility = View.GONE
        }
    }

    class TrackItemHolder(
            parent: ViewGroup?,
            onClick: (AudioTrack) -> Unit,
            musicService: MusicService?
    ) : BaseViewHolder(parent, R.layout.track_item) {
        private lateinit var track: AudioTrack

        init {
            itemView.setOnClickListener {
                onClick(track)
            }
            itemView.track_playing_now.mediaSession = musicService?.mediaSession
        }

        fun update(track: AudioTrack) {
            this.track = track
            itemView.apply {
                track_last_seen.text = DateHelper.getLongPastDate(track.publishedAt, context)
                channel_name.text = track.channelTitle
                track_name.text = track.title
                track_time.text = DateHelper.getTime(track.lengthInSeconds)
                track_playing_now.trackListenerCount = track.listenCount
                track_playing_now.trackId = track.id.toString()
                track_logo.loadImage(track.imageUrl)
            }
        }

    }
}