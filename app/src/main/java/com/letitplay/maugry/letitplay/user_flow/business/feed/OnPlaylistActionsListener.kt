package com.letitplay.maugry.letitplay.user_flow.business.feed

import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel


interface OnPlaylistActionsListener {
    fun performPushToTop(feedItem: ExtendTrackModel)
    fun performPushToBottom(feedItem: ExtendTrackModel)
}