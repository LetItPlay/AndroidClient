package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey


class FeedKey : BaseKey() {
    override fun createFragment(): BaseFragment = FeedFragment()
}