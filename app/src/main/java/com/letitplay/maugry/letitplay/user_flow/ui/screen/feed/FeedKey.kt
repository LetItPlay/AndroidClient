package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Parcelable
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import paperparcel.PaperParcel

@PaperParcel
object FeedKey : BaseKey() {
    override fun createFragment(): BaseFragment = FeedFragment()

    @JvmField val CREATOR: Parcelable.Creator<FeedKey> = PaperParcelFeedKey.CREATOR

    override fun toString(): String = this::class.java.name
}