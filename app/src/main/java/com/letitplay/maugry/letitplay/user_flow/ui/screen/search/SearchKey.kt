package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey


object SearchKey : BaseKey() {

    override fun isRootFragment(): Boolean = true

    override fun createFragment(): BaseFragment = SearchFragment()

}