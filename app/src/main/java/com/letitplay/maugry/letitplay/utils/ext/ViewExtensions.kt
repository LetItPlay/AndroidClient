package com.letitplay.maugry.letitplay.utils.ext

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.View


fun View.show() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.hide() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

inline val RecyclerView.defaultItemAnimator
    get() = itemAnimator as DefaultItemAnimator