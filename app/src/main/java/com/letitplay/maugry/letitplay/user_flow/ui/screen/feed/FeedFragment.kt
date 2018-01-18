package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedAdapter
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import kotlinx.android.synthetic.main.feed_fragment.*

class FeedFragment : BaseFragment<FeedPresenter>(R.layout.feed_fragment, FeedPresenter) {

    private val feedListAdapter = FeedAdapter()
    private var feedRepo: MusicRepo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feed_list.apply {
            adapter = feedListAdapter.apply {
                musicService = this@FeedFragment.musicService
                onLikeClick = this@FeedFragment::onLikeClick
                onClickItem = { playTrack(it) }
            }
            layoutManager = LinearLayoutManager(context)
        }
        presenter?.loadTracks {
            if (presenter.extendTrackList?.size != 0) {
                presenter.extendTrackList?.let {
                    feedListAdapter.data = it
                }
            } else {
                swipe_refresh.isEnabled = false
                feed_no_tracks.visibility = View.VISIBLE
            }
        }
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh.setOnRefreshListener {
            presenter?.loadTracksFromRemote(
                    { _, _ ->
                        swipe_refresh.isRefreshing = false
                    },
                    {
                        if (presenter.extendTrackList?.size != 0) {
                            presenter.extendTrackList?.let {
                                feedListAdapter.data = it
                            }
                        } else {
                            swipe_refresh.isEnabled = false
                            feed_no_tracks.visibility = View.VISIBLE
                        }
                        swipe_refresh.isRefreshing = false
                    }
            )
        }
    }

    private fun onLikeClick(extendTrack: ExtendTrackModel, isLiked: Boolean, position: Int) {
        val like: LikeModel = if (isLiked) LikeModel(-1, 1, 1)
        else LikeModel(1, 1, 1)
        extendTrack.track?.id?.let {
            presenter?.updateFavouriteTracks(extendTrack, like) {
                presenter.updatedTrack?.let {
                    feedListAdapter.notifyItemChanged(position)
                }
            }
        }
    }

    private fun playTrack(trackId: Long) {
        if (feedRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
        presenter?.playlist?.let {
            feedRepo = MusicRepo(it)
        }
        (activity as NavigationActivity).updateRepo(trackId, feedRepo)
    }
}
