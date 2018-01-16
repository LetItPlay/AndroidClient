package com.letitplay.maugry.letitplay.user_flow.business.player

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DataHelper
import kotlinx.android.synthetic.main.track_item.view.*

class PlayListAdapter : RecyclerView.Adapter<PlayListAdapter.TrackItemHolder>() {

    var data: List<AudioTrack> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onClickItem: ((Long) -> Unit)? = null
    var musicService: MusicService? = null

    override fun onBindViewHolder(holder: TrackItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClickItem?.invoke(data[position].id) }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TrackItemHolder {
        return TrackItemHolder(parent)
                .apply {
            itemView.track_playing_now.mediaSession = musicService?.mediaSession
        }
    }

    class TrackItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.track_item) {

        fun update(track: AudioTrack) {
            itemView.apply {
                track_last_seen.text = DataHelper.getData(track.publishedAt, context)
                channel_name.text = track.channelTitle
                track_name.text = track.title
                track_time.text = DataHelper.getTime(track.length)
                track_playing_now.trackListenerCount = track.listenCount
                track_playing_now.trackUrl = track.url
                Glide.with(context)
                        .load(track.imageUrl)
                        .into(track_logo)
            }
        }

    }
}