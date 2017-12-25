package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey


object ProfileKey : BaseKey() {

    override fun isRootFragment(): Boolean = true

    override fun createFragment(): BaseFragment = ProfileFragment()

}