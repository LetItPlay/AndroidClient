package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalog

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import kotlinx.android.synthetic.main.catalog_item.view.*

class CatalogAdapter(private val seeAllClick: ((Int) -> Unit),
                     private val onChannelClick: ((Int) -> Unit)) : RecyclerView.Adapter<CatalogAdapter.CatalogItemHolder>() {

    var categories: List<Category> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogItemHolder = CatalogItemHolder(parent)

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CatalogItemHolder, position: Int) {
        holder.update(categories[position])
    }

    inner class CatalogItemHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.catalog_item) {

        fun update(category: Category) {
            itemView.apply {
                if (category.stations != null) {
                    catalog_see_all.visibility = View.VISIBLE
                    val type = if (category.id == -1) CategoryAdapter.FAVOURITE else CategoryAdapter.CATEGORY
                    catalog_item.adapter = CategoryAdapter(type, onChannelClick).also {
                        it.channels = category.stations
                    }
                    catalog_name.text = category.name
                    catalog_see_all.setOnClickListener {
                        seeAllClick(category.id)
                    }
                }
            }
        }
    }
}