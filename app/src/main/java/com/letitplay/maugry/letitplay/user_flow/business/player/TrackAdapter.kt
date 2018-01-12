package com.letitplay.maugry.letitplay.user_flow.business.player

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackItemHolder>() {

    private var data: List<TrackModel> = ArrayList()

    var onClick: (() -> Unit)? = null

    override fun onBindViewHolder(holder: TrackItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClick?.invoke() }
        }
    }

    fun setData(channelList: List<TrackModel>) {
        channelList.let {
            data = it
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TrackItemHolder = TrackItemHolder(parent)

    class TrackItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.track_item) {

        fun update(track: TrackModel) {
            itemView.apply {
            }
        }

    }
}