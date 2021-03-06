package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import kotlinx.android.synthetic.main.playlist_fragment.*
import timber.log.Timber

class PlaylistFragment : BaseFragment(R.layout.playlist_fragment), MusicService.RepoChangesListener {

    private lateinit var trackAdapter: TrackAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val tracksRecycler = view.findViewById<RecyclerView>(R.id.tracks_list)
        trackAdapter = TrackAdapter(musicService, ::onOtherClick, ::playTrack)
        tracksRecycler.apply {
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(listDivider(tracksRecycler.context, R.drawable.list_divider))
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (musicService?.musicRepo != null) {
            onRepoChanged(musicService?.musicRepo)
        }
        musicService?.addRepoChangesListener(this)
        header.attachTo(tracks_list)
    }

    private fun playTrack(track: AudioTrack) {
        if (musicService?.musicRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(track.id)
            return
        }
    }

    private fun onOtherClick(trackId: Int, reason: Int) {
        // vm.onReportClick(trackId, reason)
    }


    override fun onRepoChanged(repo: MusicRepo?) {
        Timber.d("Music repo changed")
        trackAdapter.data = repo?.playlist ?: emptyList()
        repo?.playlist?.let {
            track_playlist_count?.text = it.size.toString()
            track_playlist_time?.text = DateHelper.getTime(it.sumBy { it.lengthInSeconds })
        }
    }
}