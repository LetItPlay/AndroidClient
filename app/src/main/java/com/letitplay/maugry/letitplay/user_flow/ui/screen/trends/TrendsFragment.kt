package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.FeedItemModel
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
            presenter.feedItemList?.let {
                trendsListAdapter.data = it
                trendsListAdapter.musicService = musicService
                trendsListAdapter.onClickItem = { playTrack(it) }
                trendsListAdapter.onLikeClick = this::onLikeClick
            }
        }
    }

    private fun onLikeClick(feedItem: FeedItemModel, isLiked: Boolean, position:Int) {
        var like: LikeModel
        if (isLiked) like = LikeModel(-1, 1, 1)
        else like = LikeModel(1, 1, 1)
        feedItem.track?.id?.let {
            presenter?.updateFavouriteTracks(it.toInt(), like) {
                presenter.updatedTrack?.let {
                    var track: FavouriteTracksModel = FavouriteTracksModel().query { it.equalTo("id", feedItem.track?.id) }.first()
                    track.likeCounts = it.likeCount
                    track.isLiked = !isLiked
                    track.save()
                    feedItem.like = track
                    trendsListAdapter.notifyItemChanged(position)
                }
            }

        }
    }

    private fun playTrack(trackId: Long) {
        if (trendsRepo != null) {
            (activity as NavigationActivity).musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
        val playlist = presenter?.feedItemList?.map {
            AudioTrack(
                    id = it.track?.id!!,
                    url = "${GL_MEDIA_SERVICE_URL}${it.track?.audio?.fileUrl}",
                    title = it.track?.name,
                    subtitle = it.channel?.name,
                    imageUrl = "${GL_MEDIA_SERVICE_URL}${it.track?.image}",
                    channelTitle = it.track?.name,
                    length = it.track?.audio?.lengthInSeconds,
                    listenCount = it.track?.listenCount,
                    publishedAt = it.track?.publishedAt
            )
        } ?: return

        trendsRepo = MusicRepo(playlist)
        (activity as NavigationActivity).updateRepo(trackId, trendsRepo)
    }

}