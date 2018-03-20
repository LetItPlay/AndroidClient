package ru.rambler.android.swipe_layout

import ru.rambler.libs.swipe_layout.SwipeLayout


interface SimpleOnSwipeListener: SwipeLayout.OnSwipeListener {
    override fun onRightStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean) {}

    override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean) {}

    override fun onLeftStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean) {}

    override fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean) {}

    override fun onPositionChanged(swipeLayout: SwipeLayout?, moveToRight: Boolean, left: Int) {}
}