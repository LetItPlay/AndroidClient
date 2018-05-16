package com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toAudioTrack
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.BeginSwipeHandler
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.gone
import com.letitplay.maugry.letitplay.utils.ext.hide
import com.letitplay.maugry.letitplay.utils.ext.show
import kotlinx.android.synthetic.main.user_playlist_fragment.*
import ru.rambler.libs.swipe_layout.SwipeLayout


class UserPlaylistFragment : BaseFragment(R.layout.user_playlist_fragment) {

    private val playlistAdapter: PlaylistAdapter by lazy {
        PlaylistAdapter(musicService, ::playTrack, ::onOtherClick, ::onSwipeReached, ::onRemoveClick, ::onPlaylistClear)
    }

    private var playlistsRepo: MusicRepo? = null

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(PlaylistsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.isReported.observe(this, Observer<Boolean> {
            it?.let {
                if (it) Toast.makeText(ctx, R.string.report_message, Toast.LENGTH_LONG).show()
            }
        })

        vm.state.observe(this, Observer<PlaylistsViewModel.ViewState> {
            if (it != null) {
                when {
                    it.showTracks -> {
                        playlistAdapter.data = it.tracks
                        playlist_no_tracks.hide()
                        playlist_track_list.show()
                    }
                    else -> {
                        playlist_track_list.hide()
                        playlist_no_tracks.show()
                    }
                }
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val playlistRecycler = view.findViewById<RecyclerView>(R.id.playlist_track_list)
        val beginSwipeHandler = BeginSwipeHandler(playlistRecycler)
        playlistAdapter.onBeginSwipe = beginSwipeHandler::onSwipeBegin
        playlistRecycler.adapter = playlistAdapter
        val divider = listDivider(playlistRecycler.context, R.drawable.list_divider)
        playlistRecycler.addItemDecoration(divider)
        return view
    }

    private fun onRemoveClick(track: Track, position: Int, swipeLayout: SwipeLayout) {
        // TODO: Move it to viewmodel !
        swipeLayout.animateReset()
        if (musicService?.musicRepo?.currentAudioTrack?.id == track.id) {
            navigationActivity.musicPlayerSmall?.apply {
                val audioNext: AudioTrack? = musicService?.musicRepo?.nextAudioTrack
                stop()
                if (audioNext != null) {
                    skipToQueueItem(audioNext.id)
                }
            }
        }
        vm.deleteTrack(track)
        navigationActivity.removeTrack(position)
    }


    private fun onSwipeReached(track: Track, position: Int, swipeLayout: SwipeLayout) {
        onRemoveClick(track, position, swipeLayout)
    }

    private fun onPlaylistClear() {
        // TODO: Move it to viewmodel !
        if (musicService?.musicRepo == playlistsRepo) {
            navigationActivity.musicPlayerSmall?.apply {
                stop()
                navigationActivity.updateRepo(-1, null, emptyList())
                gone()
            }
        }
        vm.clearPlaylist()
    }

    private fun playTrack(track: Track) {
        val trackId = track.id
        if (playlistsRepo != null && musicService?.musicRepo?.getAudioTrackAtId(trackId) != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(track.id)
            return
        }
        vm.state.value?.tracks?.let {
            playlistsRepo = MusicRepo(it.map(TrackWithChannel::toAudioTrack).toMutableList(), true)
            navigationActivity.updateRepo(track.id, playlistsRepo, it)
        }
    }

    private fun onOtherClick(trackId: Int, reason: Int) {
        vm.onReportClick(trackId, reason)
    }

}