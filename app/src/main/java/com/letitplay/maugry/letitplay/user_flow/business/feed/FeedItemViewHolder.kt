package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.support.transition.TransitionManager
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.utils.ext.*
import kotlinx.android.synthetic.main.feed_item.view.*
import kotlinx.android.synthetic.main.view_feed_card.view.*
import kotlinx.android.synthetic.main.view_feed_card_info.view.*
import ru.rambler.android.swipe_layout.SimpleOnSwipeListener
import ru.rambler.libs.swipe_layout.SwipeLayout

class FeedItemViewHolder(
        parent: ViewGroup?,
        playlistActionsListener: OnPlaylistActionsListener?,
        onClick: (TrackWithChannel) -> Unit,
        onLikeClick: (TrackWithChannel) -> Unit,
        onBeginSwipe: (SwipeLayout) -> Unit,
        musicService: MusicService?
) : BaseViewHolder(parent, R.layout.feed_item) {
    lateinit var feedData: TrackWithChannel

    init {
        val addToTop = {
            playlistActionsListener?.performPushToTop(feedData)
            itemView.feed_swipe_layout.animateReset()
            showOverlay()
        }
        val addToBottom = {
            playlistActionsListener?.performPushToBottom(feedData)
            itemView.feed_swipe_layout.animateReset()
            showOverlay()
        }
        val gestureOnSwipeDetector = GestureDetectorCompat(itemView.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                showInfo()
                super.onLongPress(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClick(feedData)
                }
                return super.onSingleTapConfirmed(e)
            }
        })
        itemView.apply {
            feed_swipe_layout.setOnTouchListener { _, motionEvent -> gestureOnSwipeDetector.onTouchEvent(motionEvent) }
            feed_track_info_title.setOnClickListener {
                hideInfo()
            }
            feed_track_info_description.setOnClickListener {
                hideInfo()
            }

            feed_like.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onLikeClick(feedData)
                }
            }
            left_menu.setOnClickListener {
                addToTop()
            }
            right_menu.setOnClickListener {
                addToBottom()
            }
            feed_playing_now.mediaSession = musicService?.mediaSession
            feed_swipe_layout.setOnSwipeListener(object : SimpleOnSwipeListener {
                override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                    onBeginSwipe(swipeLayout)
                }

                override fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                    if (moveToRight)
                        addToBottom()
                    else
                        addToTop()
                }
            })
        }
    }


    fun update(feedData: TrackWithChannel?) {
        if (feedData == null) {
            return
        }
        this.feedData = feedData
        itemView.apply {
            val data = DateHelper.getLongPastDate(feedData.track.publishedAt, context)
            feed_card.show()
            feed_card_info.gone()
            feed_track_info_logo.gone()
            feed_track_info_title.text = feedData.track.title
            feed_track_info_description.text = feedData.track.description?.let {
                if (it.isHtml()) it.toHtml() else it
            } ?: ""

            feed_playing_now.trackListenerCount = feedData.track.listenCount
            feed_playing_now.trackId = feedData.track.id.toString()
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
        this.feedData = feedData
        itemView.feed_like.isLiked = feedData.isLike
        itemView.feed_like.likeCount = feedData.track.likeCount
    }

    private fun showOverlay() {
        val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.overlay_with_delay)
        itemView.track_added_overlay.startAnimation(animation)
    }

    private fun showInfo() {
        itemView.apply {
            (feed_card_info as? NestedScrollView)?.scrollTo(0, 0)
            TransitionManager.beginDelayedTransition(this as ViewGroup)
            feed_track_info_logo.show()
            feed_card.hide()
            feed_card_info.show()
        }
    }

    private fun hideInfo() {
        itemView.apply {
            TransitionManager.beginDelayedTransition(this as ViewGroup)
            feed_track_info_logo.hide()
            feed_card_info.gone()
            feed_card.show()
        }
    }
}