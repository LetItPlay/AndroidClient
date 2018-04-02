package com.letitplay.maugry.letitplay.user_flow.ui.screen.player

import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.MotionEvent
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.track_detail_fragment.*
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import android.text.Selection
import android.R.attr.onClick
import android.text.style.ClickableSpan
import android.text.Layout
import android.text.Spanned
import android.widget.TextView




class TrackDetailFragment : BaseFragment(R.layout.track_detail_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        player_track_description.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View, event: MotionEvent): Boolean {

                var text = (view as TextView).text
                if (text is Spanned) {
                    val buffer = text as Spannable

                    val action = event.action

                    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                        var x = event.x
                        var y = event.y

                        x -= player_track_description.totalPaddingLeft
                        y -= player_track_description.totalPaddingTop

                        x += player_track_description.scrollX
                        y += player_track_description.scrollY

                        val layout = player_track_description.layout
                        val line = layout.getLineForVertical(y.toInt())
                        val off = layout.getOffsetForHorizontal(line, x.toFloat())

                        val link = buffer.getSpans(off, off,
                                ClickableSpan::class.java)

                        if (link.size != 0) {
                            if (action == MotionEvent.ACTION_UP) {
                                link[0].onClick(player_track_description)
                            } else if (action == MotionEvent.ACTION_DOWN) {
                                Selection.setSelection(buffer,
                                        buffer.getSpanStart(link[0]),
                                        buffer.getSpanEnd(link[0]))
                            }
                            return true
                        }
                    }
                 }

                view.onTouchEvent(event)
                return false
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