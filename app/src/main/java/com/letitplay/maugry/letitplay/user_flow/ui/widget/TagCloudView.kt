package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.letitplay.maugry.letitplay.R
import kotlinx.android.synthetic.main.tag_cloud.view.*

typealias Tag = String

class TagCloudView : FrameLayout {
    private var tags: List<Tag> = emptyList()
    var removeNotFullVisible: Boolean = false

    init {
        LayoutInflater.from(context).inflate(R.layout.tag_cloud, this)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (removeNotFullVisible) {
            for (i in 0 until tag_cloud_flex.childCount) {
                val child = tag_cloud_flex.getChildAt(i)
                val childBottom = child.bottom
                if (childBottom >= height) {
                    child.visibility = View.GONE
                }
            }
        }
    }

    fun setTagList(tags: List<Tag>) {
        if (this.tags == tags) return
        post {
            tag_cloud_flex.removeAllViews()
            this.tags = tags
            tags.forEach {
                val view: TextView = LayoutInflater.from(context).inflate(R.layout.channel_tag, this, false) as TextView
                view.text = it
                tag_cloud_flex.addView(view)
            }
        }
    }
}