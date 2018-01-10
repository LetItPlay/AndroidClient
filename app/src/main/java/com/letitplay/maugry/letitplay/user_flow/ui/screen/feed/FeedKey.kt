package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.annotation.SuppressLint
import android.os.Parcelable
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class FeedKey : BaseKey(), Parcelable {
    override fun isRootFragment(): Boolean = true

    override fun createFragment(): BaseFragment<BasePresenter<IMvpView>> = FeedFragment()
}