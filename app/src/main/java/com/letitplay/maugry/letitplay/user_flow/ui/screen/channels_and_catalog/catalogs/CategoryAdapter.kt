package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalogs

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.category_item_holder.view.*

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryItemHolder>() {

    var channels: List<Channel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemHolder = CategoryItemHolder(parent)

    override fun getItemCount(): Int = channels.size

    override fun onBindViewHolder(holder: CategoryItemHolder, position: Int) {
        holder.update(channels[position])
    }

    inner class CategoryItemHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.category_item_holder) {

        fun update(channel: Channel) {
            itemView.apply {
                category_channel_title.text = channel.name
                category_channel_preview.loadImage(channel.imageUrl, placeholder = R.drawable.channel_placeholder)
            }
        }
    }
}