package com.letitplay.maugry.letitplay.user_flow.business.player

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import kotlinx.android.synthetic.main.track_item.view.*

class PlayListAdapter : RecyclerView.Adapter<PlayListAdapter.TrackItemHolder>() {

    var data: List<AudioTrack> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onClick: (() -> Unit)? = null

    override fun onBindViewHolder(holder: TrackItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClick?.invoke() }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TrackItemHolder = TrackItemHolder(parent)

    class TrackItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.track_item) {

        fun update(track: AudioTrack) {
            itemView.apply {
                channel_name.text = track.channelTitle
                track_name.text = track.title
                track_time.text = track.length.toString()
                track_listen_count.text = track.listenCount.toString()
                Glide.with(context)
                        .load(track.imageUrl)
                        .into(track_logo)
            }
        }

    }
}