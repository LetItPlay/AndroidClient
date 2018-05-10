package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalogs

import android.annotation.SuppressLint
import android.os.Bundle
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.MenuType
import com.zhuinden.simplestack.navigator.StateKey
import com.zhuinden.simplestack.navigator.ViewChangeHandler
import com.zhuinden.simplestack.navigator.changehandlers.SegueViewChangeHandler
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class CategoryPageKey(private val channelId: Int) : BaseKey(), StateKey {
    override fun menuType(): MenuType = MenuType.CATEGORYPAGE

    override fun isRootFragment(): Boolean = false

    override fun createFragment(): BaseFragment {
        val fragment: BaseFragment = CategoryPageFragment()

        val bundle: Bundle? = fragment.arguments ?: Bundle()
        bundle?.putInt("KEY", channelId)
        fragment.arguments = bundle

        return fragment
    }

    override fun layout(): Int = R.layout.category_page_fragments

    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
}