package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey


class FeedTestKey : BaseKey() {
    override fun isRootFragment(): Boolean = false

    override fun createFragment(): BaseFragment = FeedTestFragment()
}