package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.annotation.SuppressLint
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.MenuType
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
class TrackKey : BaseKey() {

    override fun isRootFragment(): Boolean = false

    override fun menuType(): MenuType = MenuType.PLAYER

    override fun createFragment(): BaseFragment = TrackFragment()
}