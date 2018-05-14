package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalog

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.utils.ext.loadCircularImage
import com.letitplay.maugry.letitplay.utils.ext.loadImage
import kotlinx.android.synthetic.main.category_item_holder.view.*
import kotlinx.android.synthetic.main.favourite_item.view.*

class CategoryAdapter(private val type: String, private val onChannelClick: ((Int) -> Unit)) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val FAVOURITE = "favourite"
        const val CATEGORY = "category"
    }

    var channels: List<Channel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return if (type == FAVOURITE) R.layout.favourite_item else R.layout.category_item_holder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.favourite_item -> FavouriteChannelHolder(parent)
            R.layout.category_item_holder -> CategoryItemHolder(parent)
            else -> throw IllegalStateException("No such view type $viewType")
        }
    }

    override fun getItemCount(): Int = channels.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FavouriteChannelHolder -> holder.update(channels[position])
            is CategoryItemHolder -> holder.update(channels[position])
        }
    }

    inner class FavouriteChannelHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.favourite_item) {
        fun update(channel: Channel) {
            itemView.apply {
                favourite_channel_preview.loadCircularImage(channel.imageUrl)
            }
        }
    }

    inner class CategoryItemHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.category_item_holder) {
        fun update(channel: Channel) {
            itemView.apply {
                setOnClickListener {
                    onChannelClick(channel.id)
                }
                category_channel_title.text = channel.name
                category_channel_preview.loadImage(channel.imageUrl, placeholder = R.drawable.channel_placeholder)
            }
        }
    }
}