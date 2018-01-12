package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import kotlinx.android.synthetic.main.like_button.view.*


class LikeWidget : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    var like: FavouriteTracksModel? = null
        set(value) {
            field  = value
            updateState(value)
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.like_button, this)

    }

    private fun updateState(like: FavouriteTracksModel?) {
        like?.let {
            if (it.isLiked)
                feed_like_icon.setImageResource(R.drawable.like_ic)
            else feed_like_icon.setImageResource(R.drawable.dislike)
            feed_like_count.text = it.likeCounts.toString()
        }
    }

    fun isLiked() = like?.isLiked ?: false
}