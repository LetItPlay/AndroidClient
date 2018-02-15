package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.annotation.SuppressLint
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.MenuType
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class ChannelsKey : BaseKey(), StateKey {

    override fun layout(): Int = R.layout.channels_fragment

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()

    override fun menuType(): MenuType = MenuType.CHANNELS

    override fun isRootFragment(): Boolean = false

    override fun createFragment(): BaseFragment = ChannelFragment()

}