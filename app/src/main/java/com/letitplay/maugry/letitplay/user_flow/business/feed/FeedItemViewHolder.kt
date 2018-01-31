package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.widget.SwipeCallback
import com.letitplay.maugry.letitplay.user_flow.ui.widget.SwipeHorizontalLayout
import com.letitplay.maugry.letitplay.utils.ext.ifTrue
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.feed_item.view.*

class FeedItemViewHolder(parent: ViewGroup?, playlistActionsListener: OnPlaylistActionsListener?) : BaseViewHolder(parent, R.layout.feed_item) {
    lateinit var extendTrackModel: ExtendTrackModel

    init {
        val swipeLayout = itemView.findViewById<SwipeHorizontalLayout>(R.id.feed_swipe_layout)
        swipeLayout.swipeCallback = object: SwipeCallback {
            override fun onSwipeChanged(translationX: Int) {
            }

            override fun onSwipeToRight() {
                playlistActionsListener?.performPushToTop(extendTrackModel)
                        ?.ifTrue(this@FeedItemViewHolder::showOverlay)
            }

            override fun onSwipeToLeft() {
                playlistActionsListener?.performPushToBottom(extendTrackModel)
                        ?.ifTrue(this@FeedItemViewHolder::showOverlay)
            }
        }
    }

    fun update(extendTrackModel: ExtendTrackModel) {
        this.extendTrackModel = extendTrackModel
        itemView.apply {
            val data = DateHelper.getLongPastDate(extendTrackModel.track?.publishedAt, context)
            feed_like.like = extendTrackModel.like
            feed_like.isEnabled = true
            feed_playing_now.trackListenerCount = extendTrackModel.track?.listenCount
            feed_playing_now.trackUrl = "${GL_MEDIA_SERVICE_URL}${extendTrackModel.track?.audio?.fileUrl}"
            feed_time.text = DateHelper.getTime(extendTrackModel.track?.audio?.lengthInSeconds)
            feed_track_title.text = extendTrackModel.track?.name
            feed_channel_title.text = extendTrackModel.channel?.name
            feed_track_last_update.text = data
            feed_channel_logo.loadImage(extendTrackModel.channel?.imageUrl)
            feed_track_image.loadImage(extendTrackModel.track?.image)
        }
    }

    fun showOverlay() {
        val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.overlay_with_delay)
        itemView.track_added_overlay.startAnimation(animation)
    }
}