package com.letitplay.maugry.letitplay.user_flow.business

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.utils.ext.inflateHolder

abstract class BaseViewHolder(view: View?) : RecyclerView.ViewHolder(view) {

    constructor(parent: ViewGroup?, layoutId: Int) : this(parent?.inflateHolder(layoutId))
}