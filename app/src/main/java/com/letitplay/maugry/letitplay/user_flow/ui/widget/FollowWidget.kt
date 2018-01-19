package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.FollowingChannelModel
import kotlinx.android.synthetic.main.follow_button.view.*


class FollowWidget : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    var data: FollowingChannelModel? = null
        set(value) {
            field = value
            updateState(value)
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.follow_button, this)
    }


    private fun updateState(followerModel: FollowingChannelModel?) {

        followerModel?.let {
            if (it.isFollowing) {
                follow_button.setBackgroundResource(R.drawable.following_bg)
                follow_button.text = context.getString(R.string.channels_following)
                follow_button.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            } else {
                follow_button.setBackgroundResource(R.drawable.unfollowing_bg)
                follow_button.text = context.getString(R.string.channels_unfollowing)
                follow_button.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            }
        }
    }

    fun isFollow() = data?.isFollowing ?: false

}