package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedAdapter
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedPresenter
import com.letitplay.maugry.letitplay.user_flow.business.feed.OnPlaylistActionsListener
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import kotlinx.android.synthetic.main.feed_fragment.*

class FeedFragment : BaseFragment<FeedPresenter>(R.layout.feed_fragment, FeedPresenter), OnPlaylistActionsListener {

    private val feedListAdapter by lazy {
        FeedAdapter(musicService, ::playTrack, ::onLikeClick, this)
    }
    private var feedRepo: MusicRepo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (feed_list.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        feed_list.apply {
            adapter = feedListAdapter
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
        if (swipe_refresh.isRefreshing) return
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
        if (swipe_refresh.isRefreshing) return
        if (feedRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
        presenter?.playlist?.let {
            feedRepo = MusicRepo(it)
        }
        (activity as NavigationActivity).updateRepo(trackId, feedRepo)
    }

    override fun performPushToTop(feedItem: ExtendTrackModel) {
        // TODO: Call presenter method
        Toast.makeText(context, "Push to top", Toast.LENGTH_SHORT).show()
    }

    override fun performPushToBottom(feedItem: ExtendTrackModel) {
        // TODO: Call presenter method
        Toast.makeText(context, "Push to bottom", Toast.LENGTH_SHORT).show()
    }
}
