package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.annotation.SuppressLint
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class PlayerContainerKey : BaseKey() {

    override fun isRootFragment(): Boolean = false

    override fun createFragment(): BaseFragment<BasePresenter<IMvpView>> = PlayerContainerFragment()


}