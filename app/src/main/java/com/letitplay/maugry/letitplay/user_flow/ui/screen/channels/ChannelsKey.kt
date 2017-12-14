package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKay
import java.util.*


abstract class ChannelsKey : BaseKay() {

    companion object {
        fun create(): Any = UUID.randomUUID().toString()
    }

    override fun createFragment(): BaseFragment = ChannelsFragment()

}