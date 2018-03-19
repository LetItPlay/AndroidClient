package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.support.transition.TransitionManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.utils.ext.gone
import com.letitplay.maugry.letitplay.utils.ext.loadCircularImage
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import com.letitplay.maugry.letitplay.utils.ext.show
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
    private var isSwiping: Boolean = false

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

        itemView.apply {
            feed_card.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClick(feedData)
                }
            }
            feed_card.setOnLongClickListener {
                itemView.feed_track_info_scroll.smoothScrollTo(0, 0)
                TransitionManager.beginDelayedTransition(itemView as ViewGroup)
                itemView.feed_card_info.show()
                true
            }
            feed_card_info.setOnClickListener {
                TransitionManager.beginDelayedTransition(itemView as ViewGroup)
                itemView.feed_card_info.gone()
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
            feed_track_info_scroll.setOnTouchListener(object : View.OnTouchListener {
                private var shouldClick = false
                private var downX = 0f
                private var downY = 0f
                private val touchSlop = ViewConfiguration.get(itemView.context).scaledTouchSlop
                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            shouldClick = true
                            downX = event.x
                            downY = event.y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val diffY = event.y - downY
                            if (Math.abs(diffY) > touchSlop) {
                                shouldClick = false
                            }
                            view.parent.requestDisallowInterceptTouchEvent(true)
                        }
                        MotionEvent.ACTION_UP -> {
                            if (shouldClick) {
                                itemView.performClick()
                            }
                        }
                    }
                    return false
                }
            })
            feed_swipe_layout.isLeftSwipeEnabled = true
            feed_swipe_layout.setOnSwipeListener(object : SimpleOnSwipeListener {
                override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                    isSwiping = true
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
        this.feedData = feedData
        itemView.feed_like.isLiked = feedData.isLike
        itemView.feed_like.likeCount = feedData.track.likeCount
    }

    fun showOverlay() {
        val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.overlay_with_delay)
        itemView.track_added_overlay.startAnimation(animation)
    }
}