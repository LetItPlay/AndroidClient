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
import kotlinx.android.synthetic.main.playlist_item.view.*
import kotlinx.android.synthetic.main.track_item.view.*
import ru.rambler.android.swipe_layout.SimpleOnSwipeListener
import ru.rambler.libs.swipe_layout.SwipeLayout


class PlaylistsAdapter(
        private val musicService: MusicService? = null,
        private val onClickItem: (Track) -> Unit,
        private val onBeginSwipe: (SwipeLayout) -> Unit,
        private val onSwipeReached: (Track, Int, SwipeLayout) -> Unit,
        private val onRemoveClick: (Track, Int, SwipeLayout) -> Unit
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistItemHolder>() {

    var data: List<TrackWithChannel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: PlaylistItemHolder, position: Int) {
        holder.update(data[position])
    }

    override fun getItemId(position: Int): Long {
        return data[position].track.id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistItemHolder {
        return PlaylistItemHolder(parent, musicService, onClickItem, onBeginSwipe, onSwipeReached, onRemoveClick)
    }

    class PlaylistItemHolder(
            parent: ViewGroup?,
            musicService: MusicService?,
            onClickItem: (Track) -> Unit,
            onBeginSwipe: (SwipeLayout) -> Unit,
            onSwipeReached: (Track, Int, SwipeLayout) -> Unit,
            onRemoveClick: (Track, Int, SwipeLayout) -> Unit
    ) : BaseViewHolder(parent, R.layout.playlist_item) {
        lateinit var trackData: TrackWithChannel

        init {
            itemView.apply {
                track_playing_now.mediaSession = musicService?.mediaSession
                playlist_track_item.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onClickItem(trackData.track)
                    }
                }
                playlist_swipe_layout.setOnSwipeListener(object: SimpleOnSwipeListener {

                    override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                        onBeginSwipe(swipeLayout)
                    }

                    override fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                        onSwipeReached(trackData.track, adapterPosition, swipeLayout)
                    }
                })
                playlist_right_view.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onRemoveClick(trackData.track, adapterPosition, itemView.playlist_swipe_layout)
                    }
                }

            }
        }

        fun update(trackData: TrackWithChannel) {
            this.trackData = trackData
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