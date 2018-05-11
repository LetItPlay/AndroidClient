package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels

import android.support.v7.util.DiffUtil
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow


internal class ChannelDiffer(
        private val old: List<Channel>,
        private val new: List<Channel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = old.size
    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldIndex: Int, newIndex: Int): Boolean {
        return old[oldIndex].id == new[newIndex].id
    }

    override fun areContentsTheSame(oldIndex: Int, newIndex: Int): Boolean {
        return old[oldIndex] == new[newIndex]
    }

    override fun getChangePayload(oldIndex: Int, newIndex: Int): Any? {
        if (old[oldIndex].followed != new[newIndex].followed) {
            return ChannelAdapter.FOLLOW_CHANGED
        }
        return null
    }
}