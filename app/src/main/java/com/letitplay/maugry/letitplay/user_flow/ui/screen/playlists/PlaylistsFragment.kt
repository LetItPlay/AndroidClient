package com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.gone
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import kotlinx.android.synthetic.main.playlists_fragment.*


class PlaylistsFragment : BaseFragment(R.layout.playlists_fragment) {


    private val playlistAdapter: PlaylistsAdapter by lazy {
        PlaylistsAdapter(musicService, ::playTrack)
    }

    private var playlistsRepo: MusicRepo? = null

    private val vm by lazy {
        ViewModelProviders.of(this, ServiceLocator.viewModelFactory)
                .get(PlaylistsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val playlistRecycler = view.findViewById<RecyclerView>(R.id.playlists_list)
        playlistRecycler.adapter = playlistAdapter
        val divider = listDivider(playlistRecycler.context, R.drawable.list_divider)
        playlistRecycler.addItemDecoration(divider)
        val simpleItemTouchCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onTrackSwiped(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(playlistRecycler)
        view.findViewById<View>(R.id.playlist_clear_all).setOnClickListener {
            onPlaylistClear()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlist_header.attachTo(playlists_list)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(vm)
        vm.tracksInPlaylist.observe(this, Observer<List<TrackWithChannel>> {
            playlist_count.text = it?.count()?.toString() ?: "0"
            playlist_time.text = DateHelper.getTime(it?.sumBy { it.track.totalLengthInSeconds } ?: 0)
            it?.let {
                playlistAdapter.data = it
            }
        })
    }

    private fun onTrackSwiped(index: Int) {
        // TODO: Move it to viewmodel !
        navigationActivity.musicPlayerSmall?.next()
        vm.deleteTrackAt(index)
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
        vm.tracksInPlaylist.value?.let {
            playlistsRepo = MusicRepo(it.map(TrackWithChannel::toAudioTrack).toMutableList(), true)
        }
        navigationActivity.updateRepo(track.id, playlistsRepo)
    }
}