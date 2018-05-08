package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalogs

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import kotlinx.android.synthetic.main.catalog_item.view.*

class CatalogAdapter : RecyclerView.Adapter<CatalogAdapter.CatalogItemHolder>() {

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
                    catalog_item.addItemDecoration(listDivider(context, R.drawable.list_transparent_divider_16dp, LinearLayoutManager.HORIZONTAL))
                    catalog_item.adapter = CategoryAdapter().also {
                        it.channels = category.stations
                    }
                    catalog_name.text = category.name
                }
            }
        }
    }
}