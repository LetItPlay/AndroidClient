package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.support.v7.widget.RecyclerView
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
        private val onOtherClick: (AudioTrack) -> Unit,
        private val onClickItem: ((AudioTrack) -> Unit)
) : RecyclerView.Adapter<TrackAdapter.TrackItemHolder>() {

    var data: List<AudioTrack> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: TrackItemHolder, position: Int) {
        holder.update(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackItemHolder {
        return TrackItemHolder(parent, onClickItem, onOtherClick, musicService)
    }

    class TrackItemHolder(
            parent: ViewGroup?,
            onClick: (AudioTrack) -> Unit,
            onOtherClick: (AudioTrack) -> Unit,
            musicService: MusicService?
    ) : BaseViewHolder(parent, R.layout.track_item) {
        private lateinit var track: AudioTrack

        init {
            itemView.setOnClickListener {
                onClick(track)
            }
            itemView.track_other.setOnClickListener {
                onOtherClick(track)
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
                track_logo.loadImage(track.imageUrl, placeholder = R.drawable.feed_item_placeholder)
            }
        }

    }
}