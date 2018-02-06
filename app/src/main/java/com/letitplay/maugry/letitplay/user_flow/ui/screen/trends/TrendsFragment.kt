package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.trends.TrendsAdapter
import com.letitplay.maugry.letitplay.user_flow.business.trends.TrendsPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.defaultItemAnimator
import com.letitplay.maugry.letitplay.utils.ext.toAudioTrack
import kotlinx.android.synthetic.main.trends_fragment.*


class TrendsFragment : BaseFragment<TrendsPresenter>(R.layout.trends_fragment, TrendsPresenter) {

    private val trendsListAdapter by lazy {
        TrendsAdapter(musicService,
                ::playTrack,
                ::onLikeClick,
                null,
                ::onChannelClick,
                ::seeAllChannelsClick)
    }
    private var trendsRepo: MusicRepo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val trendsRecycler = view.findViewById<RecyclerView>(R.id.trend_list)
        trendsRecycler.layoutManager = LinearLayoutManager(context)
        trendsRecycler.adapter = trendsListAdapter
        trendsRecycler.addItemDecoration(listDivider(trendsRecycler.context, R.drawable.list_divider))
        trendsRecycler.defaultItemAnimator.supportsChangeAnimations = false
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            presenter?.loadTracksAndChannelsFromRemote(
                    { _, _ ->
                        swipeRefreshLayout.isRefreshing = false
                    },
                    {
                        presenter.extendTrackList?.let {
                            trendsListAdapter.updateData(it, presenter.extendChannelList!!.map { it.channel!! })
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
            )
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.loadTracksAndChannels {
            if (presenter.extendTrackList?.size != 0) {
                presenter.extendTrackList?.let {
                    trendsListAdapter.updateData(it, presenter.extendChannelList!!.map { it.channel!! })
                }
            } else {
                swipe_refresh.isEnabled = false
                trends_no_tracks.visibility = View.VISIBLE
            }
        }
    }

    private fun onLikeClick(extendTrack: ExtendTrackModel, isLiked: Boolean, position: Int) {
        if (swipe_refresh.isRefreshing) return
        val like: UpdateRequestBody = if (isLiked) UpdateRequestBody.buildUnlikeRequest()
        else UpdateRequestBody.buildLikeRequest()
        extendTrack.track?.id?.let {
            presenter?.updateFavouriteTracks(it.toInt(), like) {
                presenter.updatedTrack?.let {
                    val track: FavouriteTracksModel = FavouriteTracksModel().query { equalTo("id", extendTrack.track?.id) }.first()
                    track.likeCounts = it.likeCount
                    track.isLiked = !isLiked
                    track.save()
                    extendTrack.like = track
                    trendsListAdapter.notifyItemChanged(position)
                }
            }
        }
    }


    private fun onChannelClick(channel: ChannelModel) {

    }

    private fun seeAllChannelsClick() {
        navigationActivity.navigateTo(ChannelsKey())
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