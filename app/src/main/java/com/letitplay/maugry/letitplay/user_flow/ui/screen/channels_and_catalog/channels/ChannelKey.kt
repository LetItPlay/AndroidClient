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
class ChannelKey(private val listType: Int) : BaseKey(), StateKey {

    override fun layout(): Int = R.layout.channels_fragment

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()

    override fun menuType(): MenuType = MenuType.CHANNELS

    override fun isRootFragment(): Boolean = false

    override fun createFragment(): BaseFragment {
        val fragment: BaseFragment = ChannelFragment()
        val bundle: Bundle? = fragment.arguments ?: Bundle()
        bundle?.putInt("KEY", listType)
        fragment.arguments = bundle
        return fragment
    }

}