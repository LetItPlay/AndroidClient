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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.loadTracks {
            if (presenter.followingChannelList?.size != 0) {
                feed_list.apply {
                    adapter = feedListAdapter
                    layoutManager = LinearLayoutManager(context)
                }
                feedListAdapter.setData(presenter.trackList)
            } else {
                feed_no_tracks.visibility = View.VISIBLE
            }
        }
    }


}
