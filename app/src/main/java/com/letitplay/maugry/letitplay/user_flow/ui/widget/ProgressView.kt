package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.letitplay.maugry.letitplay.R

class ProgressView : FrameLayout {
    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context?) {
        if (isInEditMode)
            return

        LayoutInflater.from(context).inflate(R.layout.universal_progress, this)
    }
}