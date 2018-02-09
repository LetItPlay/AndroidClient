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
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.user_flow.business.trends.TrendsAdapter
import com.letitplay.maugry.letitplay.user_flow.business.trends.TrendsPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageKey
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
            presenter?.loadTracksAndChannels(
                    false,
                    { _, _ ->
                        swipeRefreshLayout.isRefreshing = false
                    },
                    {
                        presenter.extendTrackList?.let {
                            trendsListAdapter.updateData(it, presenter.extendChannelList ?: emptyList())
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
                    trendsListAdapter.updateData(it, presenter.extendChannelList ?: emptyList())
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
            presenter?.updateFavouriteTracks(it.toInt(), extendTrack, like) {
                presenter.updatedTrack?.let {
                    trendsListAdapter.notifyItemChanged(position)
                }
            }
        }
    }


    private fun onChannelClick(channel: ChannelModel) {
        channel.id?.let {
            navigationActivity.navigateTo(ChannelPageKey(it))
        }
    }

    private fun seeAllChannelsClick() {
        navigationActivity.navigateTo(ChannelsKey())
    }

    private fun playTrack(extendTrack: ExtendTrackModel, position: Int) {
        if (swipe_refresh.isRefreshing) return

        if(extendTrack.listened == null) {
                val newListener: UpdateRequestBody = UpdateRequestBody.buildListenRequest()
                presenter?.updateListenersTracks(extendTrack, newListener) {
                    trendsListAdapter.notifyItemChanged(position)
                }
        }

        if (trendsRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(extendTrack.track?.id!!)
            return
        }
        val playlist = presenter?.extendTrackList?.map {
            (it.channel to it.track).toAudioTrack()
        } ?: return

        trendsRepo = MusicRepo(playlist)
        navigationActivity.updateRepo(extendTrack.track?.id!!, trendsRepo)
    }

}