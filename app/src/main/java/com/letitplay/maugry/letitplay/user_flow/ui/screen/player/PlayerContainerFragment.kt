package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.player.PlayerContainerAdapter
import com.letitplay.maugry.letitplay.user_flow.business.player.PlayerContainerPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedKey
import kotlinx.android.synthetic.main.player_container_fragment.*


class PlayerContainerFragment : BaseFragment<PlayerContainerPresenter>(R.layout.player_container_fragment, PlayerContainerPresenter) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        player_tabs.setupWithViewPager(player_pager)
        player_pager.adapter = PlayerContainerAdapter(childFragmentManager)
        (activity as NavigationActivity).musicPlayerSmall?.visibility = View.GONE
        (activity as NavigationActivity).navigationMenu?.visibility = View.GONE
        collapse.setOnClickListener {
            goToPreView()
        }

    }
    private fun goToPreView() {
        (activity as NavigationActivity).navigateTo(FeedKey())
    }
}