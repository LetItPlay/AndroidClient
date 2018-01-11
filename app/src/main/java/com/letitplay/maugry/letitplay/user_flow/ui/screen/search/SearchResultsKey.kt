package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import android.annotation.SuppressLint
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import com.letitplay.maugry.letitplay.user_flow.ui.MenuType
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class SearchResultsKey : BaseKey() {
    override fun isRootFragment() = false

    override fun menuType(): MenuType = MenuType.PROFILE

    override fun createFragment(): BaseFragment<BasePresenter<IMvpView>> {
        return SearchFragment()
    }
}