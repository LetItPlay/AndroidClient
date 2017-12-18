package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.os.Parcelable
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.BaseKey
import paperparcel.PaperParcel

@PaperParcel
object ChannelProfileKey : BaseKey() {
    override fun isRootFragment(): Boolean = false


    override fun createFragment(): BaseFragment = ChannelProfileFragment()

    @JvmField
    val CREATOR: Parcelable.Creator<ChannelProfileKey> = PaperParcelChannelProfileKey.CREATOR

    override fun toString(): String = this::class.java.name
}