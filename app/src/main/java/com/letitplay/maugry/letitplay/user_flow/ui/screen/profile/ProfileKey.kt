package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.os.Parcelable
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedKey
import paperparcel.PaperParcel

@PaperParcel
object ProfileKey : BaseKey() {

    override fun isRootFragment(): Boolean = true

    override fun createFragment(): BaseFragment = ProfileFragment()

    @JvmField val CREATOR: Parcelable.Creator<ProfileKey> = PaperParcelProfileKey.CREATOR

    override fun toString(): String = this::class.java.name
}