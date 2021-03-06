package com.letitplay.maugry.letitplay.user_flow.ui.screen.search.compilation

import android.annotation.SuppressLint
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.MenuType
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
class CompilationKey : BaseKey() {

    override fun isRootFragment(): Boolean = true

    override fun menuType(): MenuType = MenuType.COMPILATION

    override fun createFragment(): BaseFragment = CompilationFragment()

}