package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Parcelable
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedKey
import paperparcel.PaperParcel
import java.util.*

@PaperParcel
object ChannelsKey : BaseKey() {

    override fun createFragment(): BaseFragment = ChannelsFragment()

    @JvmField val CREATOR: Parcelable.Creator<ChannelsKey> = PaperParcelChannelsKey.CREATOR

    override fun toString(): String = this::class.java.name
}