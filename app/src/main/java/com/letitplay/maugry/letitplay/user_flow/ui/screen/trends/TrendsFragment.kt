package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.api.serviceImpl
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.repo.TrackRepositoryImpl
import com.letitplay.maugry.letitplay.user_flow.business.trends.TrendsPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.defaultItemAnimator


class TrendsFragment : BaseFragment<TrendsPresenter>(R.layout.trends_fragment, TrendsPresenter) {

    private val trendsListAdapter by lazy {
        TrendsAdapter(musicService,
                ::playTrack,
                ::onLikeClick,
                null,
                ::onChannelClick,
                ::seeAllChannelsClick)
    }
//    private var trendsRepo: MusicRepo? = null

    private val vm by lazy {
        TrendViewModel(TrackRepositoryImpl(serviceImpl))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val trendsRecycler = view.findViewById<RecyclerView>(R.id.trend_list)
        trendsRecycler.layoutManager = LinearLayoutManager(context)
        trendsRecycler.adapter = trendsListAdapter
        trendsRecycler.addItemDecoration(listDivider(trendsRecycler.context, R.drawable.list_divider))
        trendsRecycler.defaultItemAnimator.supportsChangeAnimations = false
        vm.trends.subscribe({
            trendsListAdapter.updateData(it, emptyList())
        }, {})
//        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
//        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
//        swipeRefreshLayout.setOnRefreshListener {
//            presenter?.loadTracksAndChannels(
//                    false,
//                    { _, _ ->
//                        swipeRefreshLayout.isRefreshing = false
//                    },
//                    {
//                        presenter.extendTrackList?.let {
//                            trendsListAdapter.updateData(it, presenter.extendChannelList ?: emptyList())
//                        }
//                        swipeRefreshLayout.isRefreshing = false
//                    }
//            )
//        }
        return view
    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        presenter?.loadTracksAndChannels {
//            if (presenter.extendTrackList?.size != 0) {
//                presenter.extendTrackList?.let {
//                    trendsListAdapter.updateData(it, presenter.extendChannelList ?: emptyList())
//                }
//            } else {
//                swipe_refresh.isEnabled = false
//                trends_no_tracks.visibility = View.VISIBLE
//            }
//        }
//    }
//
    private fun onLikeClick(track: Track, isLiked: Boolean, position: Int) {
//        if (swipe_refresh.isRefreshing) return
//        val like: UpdateRequestBody = if (isLiked) UpdateRequestBody.UNLIKE()
//        else UpdateRequestBody.LIKE()
//        extendTrack.feedData?.id?.let {
//            presenter?.updateFavouriteTracks(it.toInt(), extendTrack, like) {
//                presenter.updatedTrack?.let {
//                    trendsListAdapter.notifyItemChanged(position)
//                }
//            }
//        }
    }
//
//
    private fun onChannelClick(channel: Channel) {
//        channel.id?.let {
//            navigationActivity.navigateTo(ChannelPageKey(it))
//        }
    }
//
    private fun seeAllChannelsClick() {
//        navigationActivity.navigateTo(ChannelsKey())
    }
//
    private fun playTrack(track: Track, position: Int) {
//        if (swipe_refresh.isRefreshing) return
//
//        if(extendTrack.listened == null) {
//                val newListener: UpdateRequestBody = UpdateRequestBody.LISTEN()
//                presenter?.updateListenersTracks(extendTrack, newListener) {
//                    trendsListAdapter.notifyItemChanged(position)
//                }
//        }
//
//        if (trendsRepo != null) {
//            navigationActivity.musicPlayerSmall?.skipToQueueItem(extendTrack.feedData?.id!!)
//            return
//        }
//        val playlist = presenter?.extendTrackList?.map {
//            (it.channel to it.feedData).toAudioTrack()
//        } ?: return
//
//        trendsRepo = MusicRepo(playlist)
//        navigationActivity.updateRepo(extendTrack.feedData?.id!!, trendsRepo)
    }

}