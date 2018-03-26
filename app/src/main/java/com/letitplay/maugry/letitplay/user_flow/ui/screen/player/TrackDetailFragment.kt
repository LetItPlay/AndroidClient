package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.track_detail_fragment.*


class TrackDetailFragment : BaseFragment(R.layout.track_detail_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (track_detailed_widget.mediaSession == null)
            track_detailed_widget.mediaSession = musicService?.mediaSession

    }

}