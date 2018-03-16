package com.letitplay.maugry.letitplay.user_flow.ui.widget

/**
 * @author Artur Vasilov
 */
interface SwipeCallback {

    fun onSwipeChanged(translationX: Int)

    fun onSwipeToRight()

    fun onSwipeToLeft()
}