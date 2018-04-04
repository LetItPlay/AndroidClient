package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetDialog
import android.support.v4.view.PagerAdapter
import android.text.Selection
import android.text.Spannable
import android.text.Spanned
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.R.id.*
import com.letitplay.maugry.letitplay.data_management.model.PlaybackSpeed
import com.letitplay.maugry.letitplay.data_management.model.availableSpeeds
import com.letitplay.maugry.letitplay.user_flow.ui.screen.global.PlayerViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.CurrentPlaylistAdapter
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.ext.isHtml
import com.letitplay.maugry.letitplay.utils.ext.toHtml
import kotlinx.android.synthetic.main.player_container_fragment.view.*
import kotlinx.android.synthetic.main.player_fragment.view.*
import kotlinx.android.synthetic.main.track_detail_fragment.*
import kotlinx.android.synthetic.main.track_detail_fragment.view.*

class PlayerWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    var isExpanded: Boolean = false
    lateinit var onCollapseClick: () -> Unit
    lateinit var playerViewModel: PlayerViewModel
    private lateinit var currentPlaylistAdapter: CurrentPlaylistAdapter

    private val preferenceHelper = PreferenceHelper(context)

    init {
        LayoutInflater.from(context).inflate(R.layout.player_container_fragment, this)
        if (!isInEditMode) {
            player_playback_button.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(context)
                val availSpeedList = availableSpeeds()
                val currentSpeedIndex = availSpeedList.indexOf(preferenceHelper.playbackSpeed)
                val optionsDialog = PlaybackSpeedDialog(context).apply {
                    setItems(availSpeedList, currentSpeedIndex)
                    onOptionClick = { onPlaybackSpeedOptionClick(this, availSpeedList, it) }
                }
                bottomSheetDialog.setContentView(optionsDialog)
                bottomSheetDialog.show()
            }

            val pages = arrayOf(R.id.detail, R.id.player, R.id.playlist)
            player_pager.offscreenPageLimit = pages.size

            player_pager.adapter = object: PagerAdapter() {
                override fun getCount(): Int = pages.size

                override fun isViewFromObject(view: View, `object`: Any): Boolean {
                    return view == `object`
                }

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    return findViewById(pages[position])
                }
            }

            player_tabs.setupWithViewPager(player_pager)
            player_like_button.setOnClickListener {
                playerViewModel.likeCurrentTrack()
            }
            collapse.setOnClickListener {
                onCollapseClick()
            }



            detail.track_detailed_scroll.setOnTouchListener(object : View.OnTouchListener {
                private var touchX = 0f
                private var touchY = 0f
                private var link = emptyArray<ClickableSpan>()
                private var text: CharSequence = ""

                override fun onTouch(view: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {

                            text = detail.player_track_description.text
                            if (text is Spanned) {
                                val buffer = text as Spannable
                                var x = event.x
                                var y = event.y

                                x -= detail.player_track_description.totalPaddingLeft
                                y -= detail.player_track_description.totalPaddingTop

                                x += detail.player_track_description.scrollX
                                y += detail.player_track_description.scrollY

                                val layout = detail.player_track_description.layout
                                val line = layout.getLineForVertical(y.toInt())
                                val off = layout.getOffsetForHorizontal(line, x.toFloat())

                                link = buffer.getSpans(off, off, ClickableSpan::class.java)
                                if (link.size != 0) {
                                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]))
                                }
                            }

                            touchX = event.x
                            touchY = event.y
                            view.parent.requestDisallowInterceptTouchEvent(true)
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val dx = Math.abs(event.x - touchX)
                            val dy = Math.abs(event.y - touchY)
                            if ((dx == 0f || dy / dx > 1f) && (touchY > event.y || touchY < event.y && view.scrollY != 0))
                                view.parent.requestDisallowInterceptTouchEvent(true)
                            else view.parent.requestDisallowInterceptTouchEvent(false)
                        }

                        MotionEvent.ACTION_UP -> {
                            view.parent.requestDisallowInterceptTouchEvent(false)
                            if (link.size != 0) {
                                link[0].onClick(detail.player_track_description)
                            }
                        }
                    }
                    view.onTouchEvent(event)
                    return true
                }
            })
        }
    }

    private fun onPlaybackSpeedOptionClick(dialog: PlaybackSpeedDialog, options: List<PlaybackSpeed>, playbackSpeed: PlaybackSpeed) {
        val player = player.music_player_big
        player.changePlaybackSpeed(playbackSpeed.value)
        dialog.selectAt(options.indexOf(playbackSpeed))
        preferenceHelper.playbackSpeed = playbackSpeed
    }

    fun setExpandedState(musicService: MusicService?) {
        isExpanded = true
        (player as MusicPlayerBig).apply {
            mediaSession = musicService?.mediaSession
        }
    }

    fun setCollapsedState() {
        isExpanded = false
        (player as MusicPlayerBig).mediaSession = null
    }

    private fun setLikeState(isLiked: Boolean) {
        player_like_button.setImageResource(if (isLiked) R.drawable.ic_like else R.drawable.ic_dislike)
    }

    private fun setDetailedTrack(track: AudioTrack) {
        with (detail) {
            player_channel_follow.setOnClickListener {
                playerViewModel.followChannelForCurrentTrack()
            }
            track_detailed_channel_title.text = track.channelTitle
            track_detailed_track_title.text = track.title
            player_like_count.text = track.likeCount?.toString()
            player_listener_count.text = track.listenCount?.toString()
            player_track_description.text = if (track.description?.isHtml() == true) track.description?.toHtml() else track.description
                    ?: ""
            Glide.with(context)
                    .load(track.imageUrl)
                    .into(track_detailed_channel_logo)

        }
    }

    private fun setFollowState(isFollow: Boolean) {
        player_channel_follow.isFollowing = isFollow
    }

    fun setup(onCollapse: () -> Unit, vm: PlayerViewModel) {
        this.onCollapseClick = onCollapse
        this.playerViewModel = vm
        playerViewModel.currentTrackIsLiked.observeForever {
            if (it != null) {
                setLikeState(it)
            }
        }
        playerViewModel.currentChannelIsFollow.observeForever {
            if (it != null) {
                setFollowState(it)
            }
        }
        playerViewModel.currentTrack.observeForever {
            if (it != null)
                setDetailedTrack(it)
        }
    }
}