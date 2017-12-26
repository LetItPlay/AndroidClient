package com.letitplay.maugry.letitplay.user_flow.ui

import android.content.Context
import android.support.v4.app.FragmentManager

interface IMvpView {
    fun showProgress()
    fun hideProgress()

    fun destroy()
    fun restart()

    val viewFragmentManager: FragmentManager?
    val ctx: Context

    val isViewDestroying: Boolean
    val isViewDestroyed: Boolean

    val safeView: IMvpView?

    val name: String
}
