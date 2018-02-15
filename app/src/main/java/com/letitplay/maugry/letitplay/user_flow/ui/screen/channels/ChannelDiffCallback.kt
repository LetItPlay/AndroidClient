package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.support.v7.util.DiffUtil
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow


internal class ChannelDiffer(
        private val old: List<ChannelWithFollow>,
        private val new: List<ChannelWithFollow>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = old.size
    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldIndex: Int, newIndex: Int): Boolean {
        return old[oldIndex].channel.id == new[newIndex].channel.id
    }

    override fun areContentsTheSame(oldIndex: Int, newIndex: Int): Boolean {
        return old[oldIndex] == new[newIndex]
    }

    override fun getChangePayload(oldIndex: Int, newIndex: Int): Any? {
        if (old[oldIndex].isFollowing != new[newIndex].isFollowing) {
            return ChannelAdapter.FOLLOW_CHANGED
        }
        return null
    }
}