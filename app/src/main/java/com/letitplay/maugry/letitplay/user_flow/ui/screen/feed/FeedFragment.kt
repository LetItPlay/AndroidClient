package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedAdapter
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.user_flow.ui.screen.player.PlayerContainerKey
import kotlinx.android.synthetic.main.feed_fragment.*

class FeedFragment : BaseFragment<FeedPresenter>(R.layout.feed_fragment, FeedPresenter) {

    private val feedListAdapter = FeedAdapter()
    private var feedRepo: MusicRepo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.loadTracks {
            if (presenter.followingChannelList?.size != 0) {
                feed_list.apply {
                    adapter = feedListAdapter
                    layoutManager = LinearLayoutManager(context)
                }
                presenter.trackAndChannel?.let {
                    feedListAdapter.data = it
                    feedListAdapter.onClickItem = { playTrack(it) }
                }
            } else {
                feed_no_tracks.visibility = View.VISIBLE
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
                    subtitle = it.second.description,
                    imageUrl = "$GL_MEDIA_SERVICE_URL${it.second.image}",
                    channelTitle = it.first.name,
                    length = it.second.audio?.lengthInSeconds,
                    listenCount = it.second.listenCount
            )
        } ?: return

        feedRepo = MusicRepo(playlist)
        musicService?.musicRepo = feedRepo
        (activity as NavigationActivity).musicPlayerSmall?.apply {
            setOnClickListener { goToPlayerView() }
            visibility = View.VISIBLE
            mediaSession = musicService?.mediaSession
            skipToQueueItem(trackId)
        }
    }

    private fun goToPlayerView() {
        (activity as NavigationActivity).navigateTo(PlayerContainerKey())
    }
}
