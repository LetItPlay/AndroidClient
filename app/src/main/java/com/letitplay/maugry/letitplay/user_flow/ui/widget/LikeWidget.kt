package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.letitplay.maugry.letitplay.R
import kotlinx.android.synthetic.main.like_button.view.*


class LikeWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {


    var isLiked: Boolean = false
        set(value) {
            field = value
            updateState(value)
        }

    var likeCount: Int? = 0
        set(value) {
            field = value
            feed_like_count.text = likeCount.toString()
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.like_button, this)

    }

    private fun updateState(isLiked: Boolean) {
        if (isLiked) {
            setLiked()
        } else {
            setUnliked()
        }
    }

    private fun setLiked() {
        feed_like_icon.setImageResource(R.drawable.like_ic)
    }

    private fun setUnliked() {
        feed_like_icon.setImageResource(R.drawable.dislike)
    }
}