package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.arch.paging.AsyncPagedListDiffer
import android.arch.paging.PagedList
import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.ShiftedListUpdateCallback
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedItemViewHolder
import com.letitplay.maugry.letitplay.user_flow.business.feed.OnPlaylistActionsListener
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedAdapter
import ru.rambler.libs.swipe_layout.SwipeLayout


class TrendAdapter(
        private val musicService: MusicService? = null,
        private val onClickItem: (TrackWithChannel) -> Unit,
        private val onLikeClick: (TrackWithChannel) -> Unit,
        private val playlistActionsListener: OnPlaylistActionsListener? = null
) : RecyclerView.Adapter<FeedItemViewHolder>() {

    private val asyncDifferConfig = AsyncDifferConfig
            .Builder<TrackWithChannel>(FeedAdapter.TRACK_WITH_CHANNEL_COMPARATOR)
            .build()

    private val differ = AsyncPagedListDiffer<TrackWithChannel>(ShiftedListUpdateCallback(this), asyncDifferConfig)

    var onBeginSwipe: (SwipeLayout) -> Unit = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedItemViewHolder{
        return FeedItemViewHolder(
                    parent,
                    playlistActionsListener,
                    onClickItem,
                    onLikeClick,
                    { onBeginSwipe(it) },
                    musicService
            )
    }

    override fun onBindViewHolder(holder: FeedItemViewHolder, position: Int) {
             holder.update(getItem(position))

    }

    override fun getItemCount(): Int = differ.itemCount

    override fun onBindViewHolder(holder: FeedItemViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            return super.onBindViewHolder(holder, position, payloads)
        }
        if (FeedAdapter.LIKE_CHANGED in payloads) {
            holder.updateLike(getItem(position )!!)
        }
    }

    fun submitList(pagedList: PagedList<TrackWithChannel>) {
        differ.submitList(pagedList)
    }

    private fun getItem(position: Int): TrackWithChannel? = differ.getItem(position)
}