package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ViewPagerWithoutScroll @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null)
    : ViewPager(context, attributeSet) {
    override fun canScrollHorizontally(direction: Int): Boolean {
        return false
    }

    override fun canScroll(v: View?, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}