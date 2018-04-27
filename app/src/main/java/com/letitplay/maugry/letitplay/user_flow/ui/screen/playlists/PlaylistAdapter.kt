package com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.utils.SharedHelper
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.playlist_header.view.*
import kotlinx.android.synthetic.main.playlist_item.view.*
import kotlinx.android.synthetic.main.track_item.view.*
import ru.rambler.android.swipe_layout.SimpleOnSwipeListener
import ru.rambler.libs.swipe_layout.SwipeLayout


class PlaylistAdapter(
        private val musicService: MusicService? = null,
        private val onClickItem: (Track) -> Unit,
        private val onOtherClick: (TrackWithChannel, Int) -> Unit,
        private val onSwipeReached: (Track, Int, SwipeLayout) -> Unit,
        private val onRemoveClick: (Track, Int, SwipeLayout) -> Unit,
        private val onClearAll: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onBeginSwipe: (SwipeLayout) -> Unit = {}

    var data: List<TrackWithChannel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) R.layout.playlist_header else R.layout.playlist_item
    }

    override fun getItemId(position: Int): Long {
        return if (position != 0) data[position - 1].track.id.toLong() else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlaylistItemHolder -> holder.update(data[position - 1])
            is HeaderItemHolder -> {
                holder.trackCount.text = data.count().toString()
                holder.totalTime.text = DateHelper.getTime(data.sumBy { it.track.totalLengthInSeconds })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.playlist_item -> PlaylistItemHolder(
                    parent,
                    musicService,
                    onClickItem,
                    onOtherClick,
                    { onBeginSwipe(it) },
                    onSwipeReached,
                    onRemoveClick
            )
            R.layout.playlist_header -> HeaderItemHolder(parent, onClearAll)
            else -> throw IllegalStateException("No such view type $viewType")
        }
    }

    inner class HeaderItemHolder(
            parent: ViewGroup?,
            onClear: () -> Unit
    ) : BaseViewHolder(parent, R.layout.playlist_header) {
        val totalTime: TextView get() = itemView.playlist_time
        val trackCount: TextView get() = itemView.playlist_count

        init {
            itemView.playlist_clear_all.setOnClickListener {
                onClear()
            }
        }
    }

    inner class PlaylistItemHolder(
            parent: ViewGroup?,
            musicService: MusicService?,
            onClickItem: (Track) -> Unit,
            onOtherClick: (TrackWithChannel, Int) -> Unit,
            onBeginSwipe: (SwipeLayout) -> Unit,
            onSwipeReached: (Track, Int, SwipeLayout) -> Unit,
            onRemoveClick: (Track, Int, SwipeLayout) -> Unit
    ) : BaseViewHolder(parent, R.layout.playlist_item) {
        lateinit var trackData: TrackWithChannel

        val adjustedAdapterPosition
            get() = adapterPosition - 1

        init {
            itemView.apply {
                track_playing_now.mediaSession = musicService?.mediaSession
                playlist_track_item.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        playlist_swipe_layout.animateReset()
                        onClickItem(trackData.track)
                    }
                }
                playlist_swipe_layout.setOnSwipeListener(object : SimpleOnSwipeListener {

                    override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                        onBeginSwipe(swipeLayout)
                    }

                    override fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            onSwipeReached(trackData.track, adjustedAdapterPosition, swipeLayout)
                        }
                    }
                })

                track_other.setOnClickListener {
                    SharedHelper.showTrackContextMenu(context, trackData, onOtherClick)
                }

                playlist_right_view.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onRemoveClick(trackData.track, adjustedAdapterPosition, itemView.playlist_swipe_layout)
                    }
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
                track_logo.loadImage(trackData.track.coverUrl, placeholder = R.drawable.feed_item_placeholder)
            }
        }
    }
}