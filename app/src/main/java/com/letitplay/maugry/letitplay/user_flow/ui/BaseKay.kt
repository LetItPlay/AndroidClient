package com.letitplay.maugry.letitplay.user_flow.ui

import android.os.Parcelable


abstract class BaseKay : Parcelable {

    abstract fun createFragment(): BaseFragment

}