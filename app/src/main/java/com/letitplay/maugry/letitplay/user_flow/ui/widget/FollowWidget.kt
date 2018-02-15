package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.letitplay.maugry.letitplay.R
import kotlinx.android.synthetic.main.follow_button.view.*
import kotlin.properties.Delegates


class FollowWidget @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.follow_button, this)
        updateState(false)
    }

    var isFollowing: Boolean by Delegates.vetoable(false, { _, old, new ->
        updateState(new)
        old != new
    })


    private fun updateState(isFollowing: Boolean) {
        if (isFollowing) {
            setUnfollow()
        } else {
            setFollow()
        }
    }

    private fun setFollow() {
        follow_button.isSelected = false
        follow_button.text = context.getString(R.string.channels_unfollowing)
    }

    private fun setUnfollow() {
        follow_button.isSelected = true
        follow_button.text = context.getString(R.string.channels_following)
    }
}