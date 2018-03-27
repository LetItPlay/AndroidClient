package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.support.v7.widget.RecyclerView
import ru.rambler.libs.swipe_layout.SwipeLayout


internal class BeginSwipeHandler(
        recyclerView: RecyclerView
): RecyclerView.OnScrollListener() {
    private var lastSwipeLayout: SwipeLayout? = null

    init {
        recyclerView.addOnScrollListener(this)
    }

    fun onSwipeBegin(swipeLayout: SwipeLayout) {
        if (swipeLayout != lastSwipeLayout)
            lastSwipeLayout?.animateReset()
        lastSwipeLayout = swipeLayout
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        lastSwipeLayout?.animateReset()
        lastSwipeLayout = null
    }
}