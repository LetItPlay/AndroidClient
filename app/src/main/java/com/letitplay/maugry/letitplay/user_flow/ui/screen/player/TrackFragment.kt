package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.player.TrackAdapter
import com.letitplay.maugry.letitplay.user_flow.business.player.TrackPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import kotlinx.android.synthetic.main.track_fragment.*
import timber.log.Timber

class TrackFragment : BaseFragment<TrackPresenter>(R.layout.track_fragment, TrackPresenter), MusicService.RepoChangesListener {

    private val trackAdapter by lazy {
        TrackAdapter().apply {
            musicService = this@TrackFragment.musicService
            onClickItem = this@TrackFragment::playTrack
        }
    }

    private val tracksLayoutManager by lazy {
        LinearLayoutManager(context).apply {
            isAutoMeasureEnabled = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tracks_list.apply {
            setHasFixedSize(true)
            adapter = trackAdapter
            layoutManager = tracksLayoutManager
        }
        if (musicService?.musicRepo != null) {
            onRepoChanged(musicService?.musicRepo)
        }
        musicService?.addRepoChangesListener(this)
        header.attachTo(tracks_list)
    }

    private fun playTrack(trackId: Long) {
        if (musicService?.musicRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
    }


    override fun onRepoChanged(repo: MusicRepo?) {
        Timber.d("Music repo changed")
        trackAdapter.data = repo?.playlist ?: emptyList()
        repo?.playlist?.let {
            track_playlist_count?.text = it.size.toString()
            track_playlist_time?.text = DateHelper.getTime(it.sumBy { it.length ?: 0 })
        }
    }
}