package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.AutoMusicService
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.utils.SharedHelper
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.track_item.view.*


class LikedTracksAdapter(
        private val musicService: AutoMusicService? = null,
        private val onClickItem: (Track) -> Unit
) : RecyclerView.Adapter<LikedTracksAdapter.ProfileItemHolder>() {

    var data: List<TrackWithChannel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ProfileItemHolder, position: Int) {
        holder.update(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileItemHolder {
        return ProfileItemHolder(parent).apply {
            itemView.track_playing_now.mediaSession = musicService?.mediaSession
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClickItem(data[adapterPosition].track)
                }
            }
        }
    }

    class ProfileItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.track_item) {
        lateinit var trackData: TrackWithChannel
        init{
            itemView.apply {
                track_other.setOnClickListener {
                    SharedHelper.showTrackContextMenu(context, trackData.track.title, trackData.channel.name, trackData.track.id)
                }
            }
        }
        fun update(trackData: TrackWithChannel) {
            this.trackData = trackData
            itemView.apply {
                    track_last_seen.text = DateHelper.getLongPastDate(trackData.track.publishedAt, context)
                    track_playing_now.trackListenerCount = trackData.track.listenCount
                    track_playing_now.trackId = trackData.track.id.toString()
                    channel_name.text = trackData.channel.name
                    track_time.text = trackData.track.trackLengthShort
                    track_name.text = trackData.track.title
                    track_logo.loadImage(trackData.track.coverUrl, placeholder = R.drawable.profile_placeholder)
            }
        }
    }
}