package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import android.annotation.SuppressLint
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.MenuType
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class SearchResultsKey : BaseKey() {
    override fun isRootFragment() = false

    override fun menuType(): MenuType = MenuType.COMPILATION

    override fun createFragment(): BaseFragment {
        return SearchFragment()
    }
}