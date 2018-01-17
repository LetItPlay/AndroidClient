package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.support.v7.widget.SearchView
import android.util.AttributeSet


class PersistenceSearchView : SearchView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onActionViewCollapsed() {
        // Not to reset query
        clearFocus()
    }
}