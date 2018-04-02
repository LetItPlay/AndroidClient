package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.track_detail_fragment.*


class TrackDetailFragment : BaseFragment(R.layout.track_detail_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        player_track_description.setOnTouchListener(object : View.OnTouchListener {
            private var touchX = 0f
            private var touchY = 0f
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touchX = event.x
                        touchY = event.y
                        view.parent.requestDisallowInterceptTouchEvent(false)
                    }

                    MotionEvent.ACTION_UP -> {
                        view.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                view.onTouchEvent(event)
                return true
            }
        })
        track_detailed_scroll.setOnTouchListener(object : View.OnTouchListener {
            private var touchX = 0f
            private var touchY = 0f
            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touchX = event.x
                        touchY = event.y
                        view.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = Math.abs(event.x - touchX)
                        val dy = Math.abs(event.y - touchY)
                        if ((dx == 0f || dy / dx > 1f) && (touchY>event.y || touchY<event.y && view.scrollY !=0))
                            view.parent.requestDisallowInterceptTouchEvent(true)
                        else view.parent.requestDisallowInterceptTouchEvent(false)
                    }
                    MotionEvent.ACTION_UP -> {
                        view.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                view.onTouchEvent(event)
                return true
            }
        })
    }
}