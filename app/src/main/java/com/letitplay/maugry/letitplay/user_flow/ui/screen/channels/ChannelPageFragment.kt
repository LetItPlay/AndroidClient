package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPageAdapter
import com.letitplay.maugry.letitplay.user_flow.business.channels.ChannelPagePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.channel_page_fragment.*

class ChannelPageFragment : BaseFragment<ChannelPagePresenter>(R.layout.channel_page_fragment, ChannelPagePresenter) {

    private var recentAddedListAdapter = ChannelPageAdapter()
    private var mostListedListAdapter = ChannelPageAdapter()
    private var withGuestsListAdapter = ChannelPageAdapter()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id: Int = getKey()
        presenter?.loadTracks(id) {
            recent_added_list.apply {
                adapter = recentAddedListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            recentAddedListAdapter.setData(presenter.vm?.sortedBy { it.listen_count })

            most_listened_list.apply {
                adapter = mostListedListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            mostListedListAdapter.setData(presenter.vm?.sortedBy { it.listen_count })
            with_guests_list.apply {
                adapter = withGuestsListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            withGuestsListAdapter.setData(presenter.vm?.sortedBy { it.listen_count })
        }

    }

    private fun goToOtherView() {
    }

}