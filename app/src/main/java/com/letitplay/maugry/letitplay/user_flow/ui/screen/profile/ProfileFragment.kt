package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.profile.ProfileAdapter
import com.letitplay.maugry.letitplay.user_flow.business.profile.ProfilePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DataHelper
import kotlinx.android.synthetic.main.profile_fragment.*


class ProfileFragment : BaseFragment<ProfilePresenter>(R.layout.profile_fragment, ProfilePresenter) {

    private val profileListAdapter = ProfileAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_list.apply {
            adapter = profileListAdapter
            layoutManager = LinearLayoutManager(context)
        }
        profile_header.attachTo(profile_list)
        presenter?.loadFavouriteTracks {
            presenter.tracksList?.let {
                profile_track_count.text = it.count().toString()
                profile_tracks_time.text = DataHelper.getTime(it.sumBy { it.audio?.lengthInSeconds ?: 0 })
                profileListAdapter.data = it
            }
        }
    }

}