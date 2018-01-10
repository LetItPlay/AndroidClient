package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.player.PlayListAdapter
import com.letitplay.maugry.letitplay.user_flow.business.player.TrackPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.track_fragment.*

class TrackFragment : BaseFragment<TrackPresenter>(R.layout.track_fragment, TrackPresenter) {

    private val trackAdapter = PlayListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val trackList: List<AudioTrack>? = musicService?.musicRepo?.playlist
        tracks_list.apply {
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(context)
        }
        trackList?.let {
            trackAdapter.data = it
        }
    }

}