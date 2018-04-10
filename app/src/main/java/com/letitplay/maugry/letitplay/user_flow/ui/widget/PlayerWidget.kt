package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetDialog
import android.support.v4.view.PagerAdapter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.App
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator.application
import com.letitplay.maugry.letitplay.data_management.model.PlaybackSpeed
import com.letitplay.maugry.letitplay.data_management.model.availableSpeeds
import com.letitplay.maugry.letitplay.user_flow.ui.screen.global.PlayerViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.CurrentPlaylistAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.ext.isHtml
import com.letitplay.maugry.letitplay.utils.ext.toHtml
import kotlinx.android.synthetic.main.player_container_fragment.view.*
import kotlinx.android.synthetic.main.player_fragment.view.*
import kotlinx.android.synthetic.main.playlist_fragment.view.*
import kotlinx.android.synthetic.main.track_detail_fragment.view.*

class PlayerWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    var isExpanded: Boolean = false
    lateinit var onCollapseClick: () -> Unit
    lateinit var playerViewModel: PlayerViewModel
    private lateinit var currentPlaylistAdapter: CurrentPlaylistAdapter

    protected val musicService: MusicService?
        get() = (application as App).musicService


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

            player_pager.adapter = object : PagerAdapter() {
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

            currentPlaylistAdapter = CurrentPlaylistAdapter(musicService, ::playTrack)
            playlist.tracks_list.apply {
                adapter = currentPlaylistAdapter
                addItemDecoration(listDivider(context, R.drawable.list_divider))
            }
        }
    }

    private fun playTrack(track: AudioTrack) {
        if (musicService?.musicRepo != null) {
            (player as MusicPlayerBig).skipToQueueItem(track.id)
        }
    }


    private fun onPlaybackSpeedOptionClick(dialog: PlaybackSpeedDialog, options: List<PlaybackSpeed>, playbackSpeed: PlaybackSpeed) {
        (player as MusicPlayerBig).changePlaybackSpeed(playbackSpeed.value)
        dialog.selectAt(options.indexOf(playbackSpeed))
        preferenceHelper.playbackSpeed = playbackSpeed
    }

    fun setExpandedState() {
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
        with(detail) {
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

    private fun setMusicRepoState(repo: MusicRepo?) {
        repo?.playlist?.let {
            currentPlaylistAdapter.data = it
        }
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

        playerViewModel.musicRepo.observeForever {
            if (it != null) {
                setMusicRepoState(it)
            }
        }
    }
}