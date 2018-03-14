package com.letitplay.maugry.letitplay.user_flow.ui.screen.search.compilation

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.CompilationModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import kotlinx.android.synthetic.main.compilation_item.view.*


class CompilationAdapter(
        private val onClick: ((CompilationModel) -> Unit)
) : RecyclerView.Adapter<CompilationAdapter.PlaylistItemHolder>() {

    var data: List<CompilationModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: PlaylistItemHolder, position: Int) {
        holder.update(data[position])
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

    class PlaylistItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.compilation_item) {
        fun update(playList: CompilationModel) {
            itemView.apply {
                playlist_title.text = playList.title
                playlist_description.text = playList.description
            }
        }
    }
}