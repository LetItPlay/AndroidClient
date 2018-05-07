package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels

import android.annotation.SuppressLint
import android.os.Bundle
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
class ChannelPageKey(private val channelId: Int) : BaseKey(), StateKey {

    override fun layout(): Int = R.layout.channel_page_fragment

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()

    override fun menuType(): MenuType = MenuType.CHANNELPAGE

    override fun isRootFragment(): Boolean = false

    override fun createFragment(): BaseFragment {
        val fragment: BaseFragment = ChannelPageFragment()

        val bundle: Bundle? = fragment.arguments ?: Bundle()
        bundle?.putInt("KEY", channelId)
        fragment.arguments = bundle

        return fragment
    }

}