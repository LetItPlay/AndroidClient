package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedItemViewHolder
import com.letitplay.maugry.letitplay.user_flow.business.feed.OnPlaylistActionsListener


class FeedAdapter(
        private val musicService: MusicService?,
        private val onClickItem: (TrackWithChannel) -> Unit,
        private val onLikeClick: (TrackWithChannel) -> Unit,
        private val playlistActionsListener: OnPlaylistActionsListener? = null
) : PagedListAdapter<TrackWithChannel, FeedItemViewHolder>(TRACK_WITH_CHANNEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedItemViewHolder {
        return FeedItemViewHolder(
                parent,
                playlistActionsListener,
                onClickItem,
                onLikeClick,
                musicService
        )
    }

    override fun onBindViewHolder(holder: FeedItemViewHolder, position: Int) {
        holder.update(getItem(position))
    }

    override fun onBindViewHolder(holder: FeedItemViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            return super.onBindViewHolder(holder, position, payloads)
        }
        if (LIKE_CHANGED in payloads) {
            getItem(position - 1)?.let {
                holder.updateLike(it)
            }
        }
    }

    companion object {
        val LIKE_CHANGED = Any()

        val TRACK_WITH_CHANNEL_COMPARATOR = object : DiffCallback<TrackWithChannel>() {
            override fun areItemsTheSame(oldItem: TrackWithChannel, newItem: TrackWithChannel) =
                    oldItem.track.id == newItem.track.id


            override fun areContentsTheSame(oldItem: TrackWithChannel, newItem: TrackWithChannel) =
                    oldItem == newItem

            override fun getChangePayload(oldItem: TrackWithChannel, newItem: TrackWithChannel): Any? {
                if (oldItem.isLike != newItem.isLike) {
                    return LIKE_CHANGED
                }
                return null
            }
        }
    }
}