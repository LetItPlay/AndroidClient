package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
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
        (activity as NavigationActivity).navigationMenu?.visibility = View.VISIBLE
        presenter?.loadTracks {
            if (presenter.trackAndChannel?.size != 0) {
                feed_list.apply {
                    adapter = feedListAdapter
                    layoutManager = LinearLayoutManager(context)
                }
                presenter.trackAndChannel?.let {
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

    private fun onLikeClick(trackId: Long?, isLiked: Boolean) {
        var like: LikeModel
        if (isLiked) like = LikeModel(-1, 1, 1)
        else like = LikeModel(1, 1, 1)
        trackId?.let {
            presenter?.updateFavouriteTracks(trackId.toInt(), like) {
                presenter.updatedTrack?.let {
                    var track: FavouriteTracksModel = FavouriteTracksModel().query { it.equalTo("id", trackId) }.first()
                    track.likeCounts = it.likeCount
                    track.isLiked = !isLiked
                    track.save()

                    presenter.trackAndChannel?.let {
                        feedListAdapter.data = it
                    }
                }
            }

        }
    }

    private fun playTrack(trackId: Long) {
        if (feedRepo != null) {
            (activity as NavigationActivity).musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
        val playlist = presenter?.trackAndChannel?.map {
            AudioTrack(
                    id = it.second.id!!,
                    url = "$GL_MEDIA_SERVICE_URL${it.second.audio?.fileUrl}",
                    title = it.second.name,
                    subtitle = it.first.name,
                    imageUrl = "$GL_MEDIA_SERVICE_URL${it.second.image}",
                    channelTitle = it.first.name,
                    length = it.second.audio?.lengthInSeconds,
                    listenCount = it.second.listenCount,
                    publishedAt = it.second.publishedAt
            )
        } ?: return

        feedRepo = MusicRepo(playlist)
        (activity as NavigationActivity).updateRepo(trackId, feedRepo)
    }
}
