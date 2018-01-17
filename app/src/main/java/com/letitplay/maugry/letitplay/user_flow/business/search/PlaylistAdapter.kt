package com.letitplay.maugry.letitplay.user_flow.business.search

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.PlaylistModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import kotlinx.android.synthetic.main.playlist_item.view.*


class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistItemHolder>() {

    private var data: List<PlaylistModel> = ArrayList()
    var onClick: ((PlaylistModel) -> Unit)? = null

    override fun onBindViewHolder(holder: PlaylistItemHolder, position: Int) {
        holder.update(data[position])
    }

    fun setData(channelList: List<PlaylistModel>) {
        channelList.let {
            data = it
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistItemHolder {
        return PlaylistItemHolder(parent).apply {
            itemView.setOnClickListener {
                if (adapterPosition != NO_POSITION) {
                    onClick?.invoke(data[adapterPosition])
                }
            }
        }
    }

    class PlaylistItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.playlist_item) {

        fun update(playlist: PlaylistModel) {
            itemView.apply {
                playlist_title.text = playlist.name
                playlist_description.text = playlist.description
            }
        }
    }
}