package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.support.v7.widget.RecyclerView
import android.transition.TransitionManager
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
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
        onClick: ((Long) -> Unit)?,
        onLikeClick: ((ExtendTrackModel, Boolean, Int) -> Unit)?,
        musicService: MusicService?
) : BaseViewHolder(parent, R.layout.feed_item) {
    lateinit var extendTrackModel: ExtendTrackModel

    init {
        val swipeLayout = itemView.findViewById<SwipeHorizontalLayout>(R.id.feed_swipe_layout)
        swipeLayout.swipeCallback = object : SwipeCallback {
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
        itemView.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (itemView.feed_card_info.isVisible()) {
                    TransitionManager.beginDelayedTransition(itemView as ViewGroup)
                    itemView.feed_card_info.gone()
                } else {
                    onClick?.invoke(extendTrackModel.track?.id!!)
                }
            }
        }
        itemView.setOnLongClickListener {
            TransitionManager.beginDelayedTransition(itemView as ViewGroup)
            itemView.feed_card_info.show()
            true
        }
        itemView.feed_like.setOnClickListener {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                it.isEnabled = false
                onLikeClick?.invoke(extendTrackModel, it.feed_like.isLiked(), adapterPosition)
            }
        }
        itemView.feed_playing_now.mediaSession = musicService?.mediaSession
    }

    fun update(extendTrackModel: ExtendTrackModel) {
        this.extendTrackModel = extendTrackModel
        itemView.apply {
            val data = DateHelper.getLongPastDate(extendTrackModel.track?.publishedAt, context)
            feed_card_info.gone()
            feed_track_info_title.text = extendTrackModel.track?.title
            feed_track_info_description.text = extendTrackModel.track?.description ?: ""
            feed_like.like = extendTrackModel.like
            feed_like.isEnabled = true
            feed_playing_now.trackListenerCount = extendTrackModel.track?.listenCount
            feed_playing_now.trackUrl = extendTrackModel.track?.audioUrl
            feed_time.text = DateHelper.getTime(extendTrackModel.track?.totalLengthInSeconds)
            feed_track_title.text = extendTrackModel.track?.title
            feed_channel_title.text = extendTrackModel.channel?.name
            feed_track_last_update.text = data
            feed_channel_logo.loadImage(extendTrackModel.channel?.imageUrl)
            feed_track_image.loadImage(extendTrackModel.track?.coverUrl)
            feed_track_info_logo.loadImage(extendTrackModel.track?.coverUrl)
        }
    }

    fun showOverlay() {
        val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.overlay_with_delay)
        itemView.track_added_overlay.startAnimation(animation)
    }
}