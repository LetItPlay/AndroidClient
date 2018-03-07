package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.paging.PagedListAdapter
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedItemViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedAdapter

class TrendAdapter(
        private val musicService: MusicService? = null,
        private val onClickItem: (TrackWithChannel) -> Unit,
        private val onLikeClick: (TrackWithChannel) -> Unit
) : PagedListAdapter<TrackWithChannel, FeedItemViewHolder>(FeedAdapter.TRACK_WITH_CHANNEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedItemViewHolder {
        return FeedItemViewHolder(
                parent,
                null,
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
        if (FeedAdapter.LIKE_CHANGED in payloads) {
            holder.updateLike(getItem(position - 1)!!)
        }
    }
}