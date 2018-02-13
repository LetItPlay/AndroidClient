package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.track_item.view.*

class TrackAdapter(
        private val musicService: MusicService? = null,
        private val onClickItem: ((Int) -> Unit)
) : RecyclerView.Adapter<TrackAdapter.TrackItemHolder>() {

    var data: List<AudioTrack> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: TrackItemHolder?, position: Int) {
        holder?.update(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TrackItemHolder {
        return TrackItemHolder(parent).apply {
            itemView.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onClickItem(data[adapterPosition].id)
                }
            }
            itemView.track_playing_now.mediaSession = musicService?.mediaSession
        }
    }

    class TrackItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.track_item) {

        fun update(track: AudioTrack) {
            itemView.apply {
                track_last_seen.text = DateHelper.getLongPastDate(track.publishedAt, context)
                channel_name.text = track.channelTitle
                track_name.text = track.title
                track_time.text = DateHelper.getTime(track.length)
                track_playing_now.trackListenerCount = track.listenCount
                track_playing_now.trackUrl = track.url
                track_logo.loadImage(track.imageUrl)
            }
        }

    }
}