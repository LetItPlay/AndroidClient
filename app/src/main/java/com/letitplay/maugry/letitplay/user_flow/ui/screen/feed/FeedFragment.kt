package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
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
        presenter?.loadTracks {
            if (presenter.extendTrackList?.size != 0) {
                feed_list.apply {
                    adapter = feedListAdapter
                    layoutManager = LinearLayoutManager(context)
                }
                presenter.extendTrackList?.let {
                    feedListAdapter.data = it
                    feedListAdapter.musicService = musicService
                    feedListAdapter.onClickItem = { playTrack(it) }
                    feedListAdapter.onLikeClick = this::onLikeClick
                }
            } else {
                feed_no_tracks.visibility = View.VISIBLE
            }
        }
    }

    private fun onLikeClick(extendTrack: ExtendTrackModel, isLiked: Boolean, position: Int) {
        var like: LikeModel
        if (isLiked) like = LikeModel(-1, 1, 1)
        else like = LikeModel(1, 1, 1)
        extendTrack.track?.id?.let {
            presenter?.updateFavouriteTracks(it.toInt(), like) {
                presenter.updatedTrack?.let {
                    var track: FavouriteTracksModel = FavouriteTracksModel().query { it.equalTo("id", extendTrack.track?.id) }.first()
                    track.likeCounts = it.likeCount
                    track.isLiked = !isLiked
                    track.save()
                    extendTrack.like = track
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
