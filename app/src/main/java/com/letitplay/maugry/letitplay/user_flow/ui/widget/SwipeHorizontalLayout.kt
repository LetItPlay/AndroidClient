package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.utils.ext.gone
import com.letitplay.maugry.letitplay.utils.ext.show

/**
 * @author Artur Vasilov
 */
class SwipeHorizontalLayout : SwipeLayout {

    private var translationAnimator: ValueAnimator? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (!isSwipeEnabled) {
            return super.onInterceptTouchEvent(event)
        }
        if (Math.abs(contentView!!.translationX) >= contentView!!.width) {
            //already closed view, ignore new events
            return super.onInterceptTouchEvent(event)
        }
        if (translationAnimator != null && translationAnimator!!.isRunning) {
            return super.onInterceptTouchEvent(event)
        }

        var isIntercepted = super.onInterceptTouchEvent(event)
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x.toInt()
                downX = lastX
                downY = event.y.toInt()
                isIntercepted = false
            }
            MotionEvent.ACTION_MOVE -> {
                val disX = (event.x - downX).toInt()
                val disY = (event.y - downY).toInt()
                isIntercepted = Math.abs(disX) > scaledTouchSlop && Math.abs(disX) > Math.abs(disY)
            }
            MotionEvent.ACTION_UP -> {
                isIntercepted = false
            }
            MotionEvent.ACTION_CANCEL -> {
                isIntercepted = false
                if (translationAnimator != null && translationAnimator!!.isRunning) {
                    translationAnimator!!.end()
                }
            }
        }
        return isIntercepted
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isSwipeEnabled) {
            return super.onTouchEvent(event)
        }
        if (translationAnimator != null && translationAnimator!!.isRunning) {
            return super.onTouchEvent(event)
        }
        if (Math.abs(contentView!!.translationX) >= contentView!!.width) {
            //already closed view, ignore new events
            return super.onTouchEvent(event)
        }

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker!!.addMovement(event)

        val dx: Int
        val dy: Int
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x.toInt()
                lastY = event.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val disX = (lastX - event.x).toInt()
                val disY = (lastY - event.y).toInt()
                if (!dragging
                        && Math.abs(disX) > scaledTouchSlop
                        && Math.abs(disX) > Math.abs(disY)) {
                    val parent = parent
                    parent?.requestDisallowInterceptTouchEvent(true)
                    dragging = true
                }
                if (dragging) {
                    val translationX = contentView!!.translationX - disX
                    if (Math.abs(translationX) > 0 && isSwipeToActionEnabled && Math.abs(translationX) < contentView!!.width) {
                        if (translationX > 0) {       // Swiping from left to right
                            rightMenuView?.gone()
                            leftMenuView?.show()
                        } else {                      // Swiping from right to left
                            leftMenuView?.gone()
                            rightMenuView?.show()
                        }
                        contentView!!.translationX = translationX
                        if (swipeCallback != null) {
                            swipeCallback!!.onSwipeChanged(translationX.toInt())
                        }
                    }
                    lastX = event.x.toInt()
                    lastY = event.y.toInt()
                }
            }
            MotionEvent.ACTION_UP -> {
                val parent = parent
                parent?.requestDisallowInterceptTouchEvent(false)
                dx = (downX - event.x).toInt()
                dy = (downY - event.y).toInt()
                dragging = false
                velocityTracker!!.computeCurrentVelocity(1000, scaledMaximumFlingVelocity.toFloat())
                val velocityX = velocityTracker!!.xVelocity.toInt()
                val velocity = Math.abs(velocityX)
                if (velocity > scaledMinimumFlingVelocity) {
                    if (isSwipeToActionEnabled && Math.abs(contentView!!.translationX) > contentView!!.width / 3) {
                        val performAction = velocityX > 0
                        if (contentView!!.translationX > 0) {
                            smoothSwipeLeftItem(performAction)
                        } else {
                            smoothSwipeRightItem(performAction)
                        }
                    } else {
                        //not clearly determined case, just judge what to do with swiped content
                        judgeOpenClose(dx, dy)
                    }
                } else {
                    //swipe is slow, just judge what to do with swiped content
                    judgeOpenClose(dx, dy)
                }
                velocityTracker!!.clear()
                velocityTracker!!.recycle()
                velocityTracker = null
                if (Math.abs(dx) > scaledTouchSlop
                        || Math.abs(dy) > scaledTouchSlop) { // ignore click listener, cancel this event
                    val motionEvent = MotionEvent.obtain(event)
                    motionEvent.action = MotionEvent.ACTION_CANCEL
                    return super.onTouchEvent(motionEvent)
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                dragging = false
                if (translationAnimator != null && translationAnimator!!.isRunning) {
                    translationAnimator!!.end()
                } else {
                    dx = (downX - event.x).toInt()
                    dy = (downY - event.y).toInt()
                    judgeOpenClose(dx, dy)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun smoothSwipeLeftItem(performAction: Boolean) {
        rightMenuView?.gone()

        if (!isSwipeToActionEnabled) {
            return
        }
        var duration = (translationDuration * (1 - contentView!!.translationX / contentView!!.width)).toInt()
        if (duration < translationDuration / 3) {
            duration = translationDuration / 3
        }
        translateContentView(duration, contentView!!.translationX, 0f,
                Runnable {
                    if (swipeCallback != null) {
                        swipeCallback!!.onSwipeToRight()
                    }
                })
    }

    private fun smoothSwipeRightItem(performAction: Boolean) {
        if (!isSwipeToActionEnabled) {
            return
        }
        var duration = (translationDuration * contentView!!.translationX / contentView!!.width).toInt()
        if (duration < translationDuration / 3) {
            duration = translationDuration / 3
        }
        translateContentView(duration, contentView!!.translationX, 0f, null)
    }

    private fun judgeOpenClose(dx: Int, dy: Int) {
        if (isSwipeToActionEnabled && contentView!!.translationX > 0) {
            if (contentView!!.translationX > contentView!!.width * swipeToActionPercent) {
                smoothSwipeLeftItem(false)
            } else {
                smoothSwipeRightItem(false)
            }
        } else {
            leftMenuView?.show()
            rightMenuView?.show()
            translateContentView(translationDuration, contentView!!.translationX, 0f, null)
        }
    }

    private fun translateContentView(duration: Int, from: Float, to: Float, endAction: Runnable?) {
        if (translationAnimator != null && translationAnimator!!.isRunning) {
            translationAnimator!!.end()
        }

        translationAnimator = ValueAnimator.ofFloat(from, to)

        translationAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                translationAnimator = null
                endAction?.run()
            }
        })
        translationAnimator!!.addUpdateListener { animation ->
            val translationX = animation.animatedValue as Float
            contentView!!.translationX = translationX
            if (swipeCallback != null) {
                swipeCallback!!.onSwipeChanged(translationX.toInt())
            }
        }
        translationAnimator!!.setDuration(duration.toLong()).start()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        isClickable = true
        contentView = findViewById(R.id.content)
        if (contentView == null) {
            throw IllegalArgumentException("Content view not found")
        }
        leftMenuView = findViewById(R.id.left_menu)
        if (leftMenuView == null) {
            throw IllegalArgumentException("Left menu view not found")
        }
        rightMenuView = findViewById(R.id.right_menu)
        if (rightMenuView == null) {
            throw IllegalArgumentException("Right menu view not found")
        }
    }

    override fun getMoveLen(event: MotionEvent): Int {
        val translationX = contentView!!.translationX
        return (event.x - translationX).toInt()
    }
}
