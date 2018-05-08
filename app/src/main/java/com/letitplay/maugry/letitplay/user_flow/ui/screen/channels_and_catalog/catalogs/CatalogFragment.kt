package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalogs

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.ChannelAndCategoriesViewModel
import com.letitplay.maugry.letitplay.utils.Result
import kotlinx.android.synthetic.main.catalogs_fragment.*
import timber.log.Timber

class CatalogFragment : BaseFragment(R.layout.catalogs_fragment) {

    private val catalogAdapter: CatalogAdapter by lazy { CatalogAdapter() }

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(ChannelAndCategoriesViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.catalog.observe(this, Observer<Result<List<Category>>> { result ->
            when (result) {
                is Result.Success -> {
                    catalogAdapter.categories = result.data
                }
                is Result.Failure -> Timber.e(result.e)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        catalog_list.adapter = catalogAdapter
    }
}