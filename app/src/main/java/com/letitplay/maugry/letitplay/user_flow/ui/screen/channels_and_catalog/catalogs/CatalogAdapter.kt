package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalogs

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import kotlinx.android.synthetic.main.catalog_item.view.*

class CatalogAdapter : RecyclerView.Adapter<CatalogAdapter.CatalogItemHolder>() {

    var data: List<Category> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogItemHolder = CatalogItemHolder(parent)

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: CatalogItemHolder, position: Int) {
        holder.update(data[position])
    }

    inner class CatalogItemHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.catalog_item) {

        fun update(category: Category) {
            itemView.apply {
                catalog_name.text = category.name
            }
        }
    }
}