package com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toAudioTrack
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.BeginSwipeHandler
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.gone
import com.letitplay.maugry.letitplay.utils.ext.hide
import com.letitplay.maugry.letitplay.utils.ext.show
import kotlinx.android.synthetic.main.playlists_fragment.*
import ru.rambler.libs.swipe_layout.SwipeLayout


class PlaylistsFragment : BaseFragment(R.layout.playlists_fragment) {

    private val playlistAdapter: PlaylistsAdapter by lazy {
        PlaylistsAdapter(musicService, ::playTrack, ::onSwipeReached, ::onRemoveClick)
    }

    private var playlistsRepo: MusicRepo? = null

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(PlaylistsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val playlistRecycler = view.findViewById<RecyclerView>(R.id.playlists_list)
        playlistAdapter.setHasStableIds(true)
        val beginSwipeHandler = BeginSwipeHandler(playlistRecycler)
        playlistAdapter.onBeginSwipe = beginSwipeHandler::onSwipeBegin
        playlistRecycler.adapter = playlistAdapter
        val divider = listDivider(playlistRecycler.context, R.drawable.list_divider)
        playlistRecycler.addItemDecoration(divider)
        view.findViewById<View>(R.id.playlist_clear_all).setOnClickListener {
            onPlaylistClear()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.state.observe(this, Observer<PlaylistsViewModel.ViewState> {
            if (it != null) {
                when {
                    it.showTracks -> {
                        playlist_count.text = it.tracks.count().toString()
                        playlist_time.text = DateHelper.getTime(it.tracks.sumBy { it.track.totalLengthInSeconds })
                        playlistAdapter.data = it.tracks
                        playlist_no_tracks.hide()
                        playlists_list.show()
                    }
                    else -> {
                        playlist_count.text = "0"
                        playlist_time.text = "00:00"
                        playlists_list.hide()
                        playlist_no_tracks.show()
                    }
                }
            }
        })
    }

    private fun onRemoveClick(track: Track, position: Int, swipeLayout: SwipeLayout) {
        // TODO: Move it to viewmodel !
        swipeLayout.animateReset()
        if (musicService?.musicRepo?.currentAudioTrack?.id == track.id)
            navigationActivity.musicPlayerSmall?.apply {
                val audioNext: AudioTrack? = musicService?.musicRepo?.nextAudioTrack
                stop()
                if (audioNext != null) {
                    skipToQueueItem(audioNext.id)
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
        navigationActivity.musicPlayerSmall?.apply {
            stop()
            navigationActivity.updateRepo(-1, null)
            gone()
        }
        vm.clearPlaylist()
    }

    private fun playTrack(track: Track) {
        val trackId = track.id
        if (playlistsRepo != null && playlistsRepo?.getAudioTrackAtId(trackId) != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(track.id)
            return
        }
        vm.state.value?.tracks?.let {
            playlistsRepo = MusicRepo(it.map(TrackWithChannel::toAudioTrack).toMutableList(), true)
        }
        navigationActivity.updateRepo(track.id, playlistsRepo)
    }

}