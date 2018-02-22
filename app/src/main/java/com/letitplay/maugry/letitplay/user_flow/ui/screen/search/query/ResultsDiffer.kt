package com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query

import android.support.v7.util.DiffUtil
import com.letitplay.maugry.letitplay.data_management.model.SearchResultItem

internal class ResultsDiffer(
        private val old: List<SearchResultItem>,
        private val new: List<SearchResultItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = old.size
    override fun getNewListSize() = new.size

    override fun areItemsTheSame(oldIndex: Int, newIndex: Int): Boolean {
        val oldItem = old[oldIndex]
        val newItem = new[newIndex]
        return when {
            oldItem is SearchResultItem.TrackItem && newItem is SearchResultItem.TrackItem -> oldItem.track.track.id == newItem.track.track.id
            oldItem is SearchResultItem.ChannelItem && newItem is SearchResultItem.ChannelItem -> oldItem.channel.channel.id == newItem.channel.channel.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldIndex: Int, newIndex: Int): Boolean {
        return old[oldIndex] == new[newIndex]
    }

    override fun getChangePayload(oldIndex: Int, newIndex: Int): Any? {
        val oldItem = old[oldIndex]
        val newItem = new[newIndex]
        when {
            oldItem is SearchResultItem.ChannelItem && newItem is SearchResultItem.ChannelItem ->
                if (oldItem.channel.isFollowing != newItem.channel.isFollowing) {
                    return SearchResultsAdapter.FOLLOW_CHANGED
                }
        }
        return null
    }
}