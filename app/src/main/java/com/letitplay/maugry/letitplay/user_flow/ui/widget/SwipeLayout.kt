package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import com.letitplay.maugry.letitplay.R

/**
 * @author Artur Vasilov
 *
 *
 * This is seriously modified version of SwipeMenu library from
 * https://github.com/TUBB/SwipeMenu
 */
abstract class SwipeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    protected var contentView: View? = null
    protected var leftMenuView: View? = null
    protected var rightMenuView: View? = null

    protected var translationDuration = DEFAULT_ANIMATION_DURATION
    protected var scaledTouchSlop: Int = 0
    protected var lastX: Int = 0
    protected var lastY: Int = 0
    protected var downX: Int = 0
    protected var downY: Int = 0

    protected var isSwipeToActionEnabled = false
    protected var swipeToActionPercent = DEFAULT_SWIPE_TO_DISMISS_PERCENT
    protected var dragging: Boolean = false
    protected var velocityTracker: VelocityTracker? = null
    protected var scaledMinimumFlingVelocity: Int = 0
    protected var scaledMaximumFlingVelocity: Int = 0
    protected var swipeMenuMaxWidth: Float = 0f

    fun isDragging(): Boolean = dragging

    protected var isSwipeEnabled = true

    var swipeCallback: SwipeCallback? = null

    init {

        if (!isInEditMode) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout, 0, defStyle)
            translationDuration = array.getInteger(R.styleable.SwipeLayout_swipe_menu_duration, DEFAULT_ANIMATION_DURATION)
            isSwipeToActionEnabled = array.getBoolean(R.styleable.SwipeLayout_swipe_to_action_enabled, true)
            swipeToActionPercent = array.getFloat(R.styleable.SwipeLayout_swipe_to_action_percent, DEFAULT_SWIPE_TO_DISMISS_PERCENT)
            swipeMenuMaxWidth = array.getDimension(R.styleable.SwipeLayout_swipe_menu_max_width, 0f)
            array.recycle()
        }
        init()
    }

    fun init() {
        val viewConfig = ViewConfiguration.get(context)
        scaledTouchSlop = viewConfig.scaledTouchSlop
        scaledMinimumFlingVelocity = viewConfig.scaledMinimumFlingVelocity
        scaledMaximumFlingVelocity = viewConfig.scaledMaximumFlingVelocity
    }

    internal abstract fun getMoveLen(event: MotionEvent): Int

    companion object {

        private val DEFAULT_ANIMATION_DURATION = 250
        private val DEFAULT_SWIPE_TO_DISMISS_PERCENT = 0.5f
    }
}