package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey


object TrendsKey : BaseKey() {

    override fun isRootFragment(): Boolean = true

    override fun createFragment(): BaseFragment = TrendsFragment()

}