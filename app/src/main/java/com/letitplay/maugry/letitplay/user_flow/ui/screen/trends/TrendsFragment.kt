package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

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
import com.letitplay.maugry.letitplay.user_flow.business.trends.TrendsPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.utils.toAudioTrack
import kotlinx.android.synthetic.main.trends_fragment.*


class TrendsFragment : BaseFragment<TrendsPresenter>(R.layout.trends_fragment, TrendsPresenter) {

    private val trendsListAdapter = FeedAdapter()
    private var trendsRepo: MusicRepo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trend_list.apply {
            adapter = trendsListAdapter.apply {
                onClickItem = { playTrack(it) }
                onLikeClick = this@TrendsFragment::onLikeClick
                musicService = this@TrendsFragment.musicService
            }
            layoutManager = LinearLayoutManager(context)
        }
        presenter?.loadTracks {
            presenter.extendTrackList?.let {
                trendsListAdapter.data = it
            }
        }

        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh.setOnRefreshListener {
            presenter?.loadTracksFromRemote(
                    { _, _ ->
                        swipe_refresh.isRefreshing = false
                    },
                    {
                        presenter.extendTrackList?.let {
                            trendsListAdapter.data = it
                        }
                        swipe_refresh.isRefreshing = false
                    }
            )
        }
    }

    private fun onLikeClick(extendTrack: ExtendTrackModel, isLiked: Boolean, position:Int) {
        if (swipe_refresh.isRefreshing) return
        val like: LikeModel = if (isLiked) LikeModel(-1, 1, 1)
        else LikeModel(1, 1, 1)
        extendTrack.track?.id?.let {
            presenter?.updateFavouriteTracks(it.toInt(), like) {
                presenter.updatedTrack?.let {
                    val track: FavouriteTracksModel = FavouriteTracksModel().query { it.equalTo("id", extendTrack.track?.id) }.first()
                    track.likeCounts = it.likeCount
                    track.isLiked = !isLiked
                    track.save()
                    extendTrack.like = track
                    trendsListAdapter.notifyItemChanged(position)
                }
            }

        }
    }

    private fun playTrack(trackId: Long) {
        if (swipe_refresh.isRefreshing) return
        if (trendsRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
        val playlist = presenter?.extendTrackList?.map {
            (it.channel to it.track).toAudioTrack()
        } ?: return

        trendsRepo = MusicRepo(playlist)
        navigationActivity.updateRepo(trackId, trendsRepo)
    }

}