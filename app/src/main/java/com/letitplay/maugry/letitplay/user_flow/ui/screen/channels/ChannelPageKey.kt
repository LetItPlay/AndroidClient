package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.annotation.SuppressLint
import android.os.Bundle
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class ChannelPageKey(private val channelId: Int) : BaseKey(), StateKey {

    override fun layout(): Int = R.layout.channel_page_fragment

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()

    override fun isRootFragment(): Boolean = false

    override fun createFragment(): BaseFragment<BasePresenter<IMvpView>> {
        val fragment: BaseFragment<BasePresenter<IMvpView>> = ChannelPageFragment()

        val bundle: Bundle? = fragment.arguments ?: Bundle()
        bundle?.putInt("KEY", channelId)
        fragment.arguments = bundle

        return fragment
    }

}