package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.remote.requests.UpdateRequestBody
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedAdapter
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedPresenter
import com.letitplay.maugry.letitplay.user_flow.business.feed.OnPlaylistActionsListener
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.ext.defaultItemAnimator
import kotlinx.android.synthetic.main.feed_fragment.*

class FeedFragment : BaseFragment<FeedPresenter>(R.layout.feed_fragment, FeedPresenter), OnPlaylistActionsListener {

    private lateinit var feedListAdapter: FeedAdapter
    private var feedRepo: MusicRepo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val feedRecycler = view.findViewById<RecyclerView>(R.id.feed_list)
        val layoutManager = LinearLayoutManager(context)
        feedListAdapter = FeedAdapter(musicService, ::playTrack, ::onLikeClick, this)
        feedRecycler.adapter = feedListAdapter
        feedRecycler.layoutManager = layoutManager
        val divider = listDivider(feedRecycler.context, R.drawable.list_divider)
        feedRecycler.addItemDecoration(divider)
        feedRecycler.defaultItemAnimator.supportsChangeAnimations = false
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.loadTracks{
            if (presenter.extendTrackList?.size != 0) {
                presenter.extendTrackList?.let {
                    feedListAdapter.data = it
                }
            } else {
                swipe_refresh.isEnabled = false
                feed_no_tracks.visibility = View.VISIBLE
            }
        }
        go_to_channels.setOnClickListener {
            seeAllChannelsClick()
        }
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh.setOnRefreshListener {
            presenter?.loadTracks(
                    false,
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

    private fun seeAllChannelsClick() {
        navigationActivity.navigateTo(ChannelsKey())
    }

    private fun onLikeClick(extendTrack: ExtendTrackModel, isLiked: Boolean, position: Int) {
        if (swipe_refresh.isRefreshing) return
        val like: UpdateRequestBody = if (isLiked) UpdateRequestBody.buildUnlikeRequest()
        else UpdateRequestBody.buildLikeRequest()
        extendTrack.track?.id?.let {
            presenter?.updateFavouriteTracks(extendTrack, like) {
                presenter.updatedTrack?.let {
                    feedListAdapter.notifyItemChanged(position)
                }
            }
        }
    }

    private fun playTrack(extendTrack: ExtendTrackModel, position: Int) {
        if (swipe_refresh.isRefreshing) return

        if (extendTrack.listened == null){
                val newListener: UpdateRequestBody = UpdateRequestBody.buildListenRequest()
                presenter?.updateListenersTracks(extendTrack, newListener) {
                    feedListAdapter.notifyItemChanged(position)
                }
        }

        if (feedRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(extendTrack.track?.id!!)
            return
        }
        presenter?.playlist?.let {
            feedRepo = MusicRepo(it)
        }
        navigationActivity.updateRepo(extendTrack.track?.id!!, feedRepo)
    }

    override fun performPushToTop(feedItem: ExtendTrackModel): Boolean {
        // TODO: Call presenter method
        return true
    }

    override fun performPushToBottom(feedItem: ExtendTrackModel): Boolean {
        // TODO: Call presenter method
        return true
    }
}
