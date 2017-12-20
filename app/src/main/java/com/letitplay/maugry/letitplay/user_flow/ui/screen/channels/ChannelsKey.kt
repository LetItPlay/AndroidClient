package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler


class ChannelsKey : BaseKey(), StateKey {

    override fun layout(): Int = R.layout.channels_fragment

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()

    override fun isRootFragment(): Boolean = true

    override fun createFragment(): BaseFragment = ChannelsFragment()

}