package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.player.PlayerContainerPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment


class PlayerContainerFragment : BaseFragment<PlayerContainerPresenter>(R.layout.player_container_fragment, PlayerContainerPresenter) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}