package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.LikeModel
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedAdapter
import com.letitplay.maugry.letitplay.user_flow.business.trends.TrendsPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.trends_fragment.*


class TrendsFragment : BaseFragment<TrendsPresenter>(R.layout.trends_fragment, TrendsPresenter) {

    private val trendsListAdapter = FeedAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.loadTracks {
            trend_list.apply {
                adapter = trendsListAdapter
                layoutManager = LinearLayoutManager(context)
            }
            presenter.trackAndChannel?.let {
                trendsListAdapter.data = it
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

}