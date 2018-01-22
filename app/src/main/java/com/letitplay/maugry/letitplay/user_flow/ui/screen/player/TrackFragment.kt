package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.player.TrackAdapter
import com.letitplay.maugry.letitplay.user_flow.business.player.TrackPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import kotlinx.android.synthetic.main.track_fragment.*

class TrackFragment : BaseFragment<TrackPresenter>(R.layout.track_fragment, TrackPresenter) {

    private val trackAdapter = TrackAdapter()
    private var playListRepo: MusicRepo? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val trackList: List<AudioTrack>? = musicService?.musicRepo?.playlist

        tracks_list.apply {
            setHasFixedSize(true)
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(context).apply {
                isAutoMeasureEnabled = true
            }
        }
        header.attachTo(tracks_list)
        trackList?.let {
            track_playlist_count.text = it.size.toString()
            track_playlist_time.text = DateHelper.getTime(it.sumBy { it.length ?: 0 })
            trackAdapter.musicService = musicService
            trackAdapter.onClickItem = { playTrack(it) }
            trackAdapter.data = it
        }
    }

    private fun playTrack(trackId: Long) {
        if (musicService?.musicRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
    }

}