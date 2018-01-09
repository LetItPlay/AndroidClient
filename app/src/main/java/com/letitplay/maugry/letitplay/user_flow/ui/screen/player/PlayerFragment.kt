package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.player.PlayerPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment


class PlayerFragment : BaseFragment<PlayerPresenter>(R.layout.player_fragment, PlayerPresenter) {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}