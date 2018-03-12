package com.letitplay.maugry.letitplay.user_flow.business.feed

import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel


interface OnPlaylistActionsListener {
    fun performPushToTop(trackData: TrackWithChannel): Boolean
    fun performPushToBottom(trackData: TrackWithChannel): Boolean
}