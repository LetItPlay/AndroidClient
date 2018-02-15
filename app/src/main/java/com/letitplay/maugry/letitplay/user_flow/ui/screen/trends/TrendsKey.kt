package com.letitplay.maugry.letitplay.user_flow.ui.screen.trends

import android.annotation.SuppressLint
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.MenuType
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
class TrendsKey : BaseKey() {

    override fun isRootFragment(): Boolean = true

    override fun menuType(): MenuType = MenuType.TRENDS

    override fun createFragment(): BaseFragment = TrendsFragment()

}