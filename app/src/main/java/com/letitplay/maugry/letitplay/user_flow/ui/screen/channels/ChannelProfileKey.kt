package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey


class ChannelProfileKey : BaseKey() {

    override fun isRootFragment(): Boolean = false

    override fun createFragment(): BaseFragment = ChannelProfileFragment()

}