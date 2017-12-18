package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.os.Bundle
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelProfileKey
import kotlinx.android.synthetic.main.profile_fragment.*


class ProfileFragment : BaseFragment(R.layout.profile_fragment) {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        home_button.setOnClickListener { (activity as NavigationActivity).navigateTo(ChannelProfileKey) }
    }
}