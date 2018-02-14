package com.letitplay.maugry.letitplay.user_flow.business.feed

import com.letitplay.maugry.letitplay.data_management.db.entity.Track


interface OnPlaylistActionsListener {
    fun performPushToTop(feedItem: Track): Boolean
    fun performPushToBottom(feedItem: Track): Boolean
}