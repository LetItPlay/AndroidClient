package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.support.transition.TransitionManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.widget.SwipeCallback
import com.letitplay.maugry.letitplay.user_flow.ui.widget.SwipeHorizontalLayout
import com.letitplay.maugry.letitplay.utils.ext.*
import kotlinx.android.synthetic.main.feed_item.view.*
import kotlinx.android.synthetic.main.view_feed_card.view.*
import kotlinx.android.synthetic.main.view_feed_card_info.view.*

class FeedItemViewHolder(
        parent: ViewGroup?,
        playlistActionsListener: OnPlaylistActionsListener?,
        onClick: (TrackWithChannel) -> Unit,
        onLikeClick: (TrackWithChannel) -> Unit,
        musicService: MusicService?
) : BaseViewHolder(parent, R.layout.feed_item) {
    lateinit var feedData: TrackWithChannel

    init {
        val swipeLayout = itemView.findViewById<SwipeHorizontalLayout>(R.id.feed_swipe_layout)
        swipeLayout.swipeCallback = object : SwipeCallback {
            override fun onSwipeChanged(translationX: Int) {
            }

            override fun onSwipeToRight() {
                playlistActionsListener?.performPushToTop(feedData.track)
                        ?.ifTrue(this@FeedItemViewHolder::showOverlay)
            }

            override fun onSwipeToLeft() {
                playlistActionsListener?.performPushToBottom(feedData.track)
                        ?.ifTrue(this@FeedItemViewHolder::showOverlay)
            }
        }
        itemView.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (itemView.feed_card_info.isVisible()) {
                    TransitionManager.beginDelayedTransition(itemView as ViewGroup)
                    itemView.feed_card_info.gone()
                } else {
                    onClick(feedData)
                }
            }
        }
        itemView.setOnLongClickListener {
            if ((itemView as SwipeHorizontalLayout).isDragging()) {
                return@setOnLongClickListener false
            }
            TransitionManager.beginDelayedTransition(itemView as ViewGroup)
            itemView.feed_card_info.show()
            true
        }
        itemView.feed_like.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                onLikeClick(feedData)
            }
        }
        itemView.feed_playing_now.mediaSession = musicService?.mediaSession
    }

    fun update(feedData: TrackWithChannel?) {
        if (feedData == null) {
            return
        }
        this.feedData = feedData
        itemView.apply {
            val data = DateHelper.getLongPastDate(feedData.track.publishedAt, context)
            feed_card_info.gone()
            feed_track_info_title.text = feedData.track.title
            feed_track_info_description.text = feedData.track.description ?: ""
            feed_playing_now.trackListenerCount = feedData.track.listenCount
            feed_playing_now.trackUrl = feedData.track.audioUrl
            feed_time.text = feedData.track.trackLengthShort
            feed_track_title.text = feedData.track.title
            feed_channel_title.text = feedData.channel.name
            feed_track_last_update.text = data
            feed_channel_logo.loadCircularImage(feedData.channel.imageUrl)
            feed_track_image.loadImage(feedData.track.coverUrl)
            feed_track_info_logo.loadImage(feedData.track.coverUrl)
        }
        updateLike(feedData)
    }

    fun updateLike(feedData: TrackWithChannel) {
        itemView.feed_like.isLiked = feedData.isLike
        itemView.feed_like.likeCount = feedData.track.likeCount
    }

    fun showOverlay() {
        val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.overlay_with_delay)
        itemView.track_added_overlay.startAnimation(animation)
    }
}