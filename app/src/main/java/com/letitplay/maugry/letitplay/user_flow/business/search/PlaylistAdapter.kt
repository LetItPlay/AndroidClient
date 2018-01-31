package com.letitplay.maugry.letitplay.user_flow.business.search

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.PlaylistModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder


class PlaylistAdapter(
        private val onClick: ((PlaylistModel) -> Unit)
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistItemHolder>() {

     var data: List<PlaylistModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: PlaylistItemHolder, position: Int) {

    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistItemHolder {
        return PlaylistItemHolder(parent).apply {
            itemView.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onClick(data[adapterPosition])
                }
            }
        }
    }

    class PlaylistItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.playlist_item)
}