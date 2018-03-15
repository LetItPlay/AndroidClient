package com.letitplay.maugry.letitplay.user_flow


import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.RecyclerView

class ShiftedListUpdateCallback(private val mAdapter: RecyclerView.Adapter<*>) : ListUpdateCallback {

    override fun onInserted(position: Int, count: Int) {
        mAdapter.notifyItemRangeInserted(position + 1, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        mAdapter.notifyItemRangeRemoved(position + 1, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        mAdapter.notifyItemMoved(fromPosition + 1, toPosition)
    }

    override fun onChanged(position: Int, count: Int, payload: Any) {
        mAdapter.notifyItemRangeChanged(position + 1, count, payload)
    }
}
