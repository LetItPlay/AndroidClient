package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedAdapter
import com.letitplay.maugry.letitplay.user_flow.business.feed.FeedPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
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
                presenter.trackList?.let {
                    feedListAdapter.data = it
                    feedListAdapter.onClickItem = { createFeedRepo(it) }
                }
                feedListAdapter.setData(presenter.trackAndChannel)
            } else {
                feed_no_tracks.visibility = View.VISIBLE
            }
        }
    }

    fun createFeedRepo(trackId: Long) {
        if (feedRepo != null) {
            return
        }
        val playlist = presenter?.trackList?.map {
            AudioTrack(
                    id = it.id!!,
                    url = "$GL_MEDIA_SERVICE_URL${it.audio_file?.file}",
                    title = it.name,
                    subtitle = it.description,
                    imageUrl = "$GL_MEDIA_SERVICE_URL${it.image}"
            )
        } ?: return

        feedRepo = MusicRepo(playlist)
        musicService?.musicRepo = feedRepo
        (activity as NavigationActivity).musicPlayerSmall?.apply {
            visibility = View.VISIBLE
            mediaSession = musicService?.mediaSession
            skipToQueueItem(trackId)
        }
    }
}
