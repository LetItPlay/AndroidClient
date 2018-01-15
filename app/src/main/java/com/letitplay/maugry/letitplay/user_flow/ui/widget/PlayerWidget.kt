package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentManager
import android.util.AttributeSet
import android.view.LayoutInflater
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.player.PlayerContainerAdapter
import kotlinx.android.synthetic.main.player_container_fragment.view.*


class PlayerWidget : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    init {
        LayoutInflater.from(context).inflate(R.layout.player_container_fragment, this)
    }

    fun setViewPager(fm: FragmentManager) {
        player_tabs.setupWithViewPager(player_pager)
        player_pager.adapter = PlayerContainerAdapter(fm)
    }
}