package com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.track_item.view.*


class PlaylistsAdapter(
        private val musicService: MusicService? = null,
        private val onClickItem: (Track) -> Unit) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistsItemHolder>() {

    var data: List<TrackWithChannel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PlaylistsItemHolder, position: Int) {
        holder.update(data[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistsItemHolder {
        return PlaylistsItemHolder(parent).apply {
            itemView.track_playing_now.mediaSession = musicService?.mediaSession
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClickItem(data[adapterPosition].track)
                }
            }
        }
    }


    class PlaylistsItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.track_item) {

        fun update(trackData: TrackWithChannel) {
            itemView.apply {
                track_last_seen.text = DateHelper.getLongPastDate(trackData.track.publishedAt, context)
                track_playing_now.trackListenerCount = trackData.track.listenCount
                track_playing_now.trackUrl = trackData.track.audioUrl
                channel_name.text = trackData.channel.name
                track_time.text = trackData.track.trackLengthShort
                track_name.text = trackData.track.title
                track_logo.loadImage(trackData.track.coverUrl)
            }
        }
    }
}