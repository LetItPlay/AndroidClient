package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage
import com.letitplay.maugry.letitplay.data_management.model.PlaylistModel
import com.letitplay.maugry.letitplay.user_flow.business.search.PlaylistAdapter
import com.letitplay.maugry.letitplay.user_flow.business.search.PlaylistPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.playlist_fragment.*
import timber.log.Timber


class PlaylistFragment : BaseFragment<PlaylistPresenter>(R.layout.playlist_fragment, PlaylistPresenter) {

    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val recycler = view.findViewById<RecyclerView>(R.id.playlist_list)
        playlistAdapter = PlaylistAdapter(::onPlaylistClick)
        recycler.adapter = playlistAdapter
        recycler.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var tag = "новости"
        if (presenter?.currentContentLang == ContentLanguage.EN) tag = "news"

        presenter?.getPlaylists {
            presenter.playlists?.let {
                if (it.firstOrNull { it.tracks.isNotEmpty() } != null) {
                    playlistAdapter.data = it
                } else {
                    playlist_no_recommendations.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun onPlaylistClick(playlist: PlaylistModel) {
        navigationActivity.updateRepo(playlist.tracks.first().id, MusicRepo(playlist.tracks))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            Timber.d("Navigate to results page")
            navigationActivity.navigateTo(SearchResultsKey())
        }
        return super.onOptionsItemSelected(item)
    }
}