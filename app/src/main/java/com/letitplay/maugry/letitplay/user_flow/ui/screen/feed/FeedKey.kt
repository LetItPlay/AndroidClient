package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey


object FeedKey : BaseKey() {
    override fun isRootFragment(): Boolean = true

    override fun createFragment(): BaseFragment = FeedFragment()
}