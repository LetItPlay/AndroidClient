package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

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
import com.letitplay.maugry.letitplay.user_flow.business.trends.TrendsPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import kotlinx.android.synthetic.main.trends_fragment.*


class TrendsFragment : BaseFragment<TrendsPresenter>(R.layout.trends_fragment, TrendsPresenter) {

    private val trendsListAdapter = FeedAdapter()
    private var trendsRepo: MusicRepo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.loadTracks {
            trend_list.apply {
                adapter = trendsListAdapter
                layoutManager = LinearLayoutManager(context)
            }
            presenter.trackAndChannel?.let {
                trendsListAdapter.data = it
                trendsListAdapter.musicService = musicService
                trendsListAdapter.onClickItem = { playTrack(it) }
                trendsListAdapter.onLikeClick = this::onLikeClick
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
                        trendsListAdapter.data = it
                    }
                }
            }

        }
    }

    private fun playTrack(trackId: Long) {
        if (trendsRepo != null) {
            (activity as NavigationActivity).musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
        val playlist = presenter?.trackAndChannel?.map {
            AudioTrack(
                    id = it.second.id!!,
                    url = "${GL_MEDIA_SERVICE_URL}${it.second.audio?.fileUrl}",
                    title = it.second.name,
                    subtitle = it.first.name,
                    imageUrl = "${GL_MEDIA_SERVICE_URL}${it.second.image}",
                    channelTitle = it.first.name,
                    length = it.second.audio?.lengthInSeconds,
                    listenCount = it.second.listenCount,
                    publishedAt = it.second.publishedAt
            )
        } ?: return

        trendsRepo = MusicRepo(playlist)
        (activity as NavigationActivity).updateRepo(trackId, trendsRepo)
    }

}