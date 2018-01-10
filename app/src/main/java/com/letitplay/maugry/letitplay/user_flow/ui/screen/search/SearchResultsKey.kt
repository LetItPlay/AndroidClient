package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import android.annotation.SuppressLint
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class SearchResultsKey : BaseKey() {
    override fun isRootFragment() = false

    override fun createFragment(): BaseFragment<BasePresenter<IMvpView>> {
        return SearchFragment()
    }
}