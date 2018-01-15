package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.player.PlayListAdapter
import com.letitplay.maugry.letitplay.user_flow.business.player.TrackPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DataHelper
import kotlinx.android.synthetic.main.track_fragment.*

class TrackFragment : BaseFragment<TrackPresenter>(R.layout.track_fragment, TrackPresenter) {

    private val trackAdapter = PlayListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var trackList: List<AudioTrack>? = null
        //Костыль потому что лагает ресайклер надо сделать постепенную загрузку данных
        musicService?.musicRepo?.playlist?.size?.let {
            if (it > 15)
                trackList = musicService?.musicRepo?.playlist?.subList(0, 15)
            else trackList = musicService?.musicRepo?.playlist?.subList(0, it)
        }
        tracks_list.apply {
            adapter = trackAdapter
            layoutManager = LinearLayoutManager(context)
        }
        trackList?.let {
            track_playlist_count.text = it.size.toString()
            track_playlist_time.text = DataHelper.getTime(it.sumBy { it.length ?: 0 })
            trackAdapter.data = it
        }
    }

}