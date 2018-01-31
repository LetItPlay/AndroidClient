package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.content.Context
import android.support.annotation.IntegerRes
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView


fun listDivider(context: Context, divRes: Int, orientation: Int = LinearLayoutManager.VERTICAL): RecyclerView.ItemDecoration {
    val dividerDecoration = DividerItemDecoration(context, orientation)
    dividerDecoration.setDrawable(context.getDrawable(divRes))
    return dividerDecoration
}