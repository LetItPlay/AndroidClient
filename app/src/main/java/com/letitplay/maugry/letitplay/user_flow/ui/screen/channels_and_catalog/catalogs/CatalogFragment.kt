package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalogs

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.ServiceLocator.router
import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.ChannelAndCategoriesViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels.ChannelKey
import com.letitplay.maugry.letitplay.utils.Result
import kotlinx.android.synthetic.main.catalogs_fragment.*
import timber.log.Timber

class CatalogFragment : BaseFragment(R.layout.catalogs_fragment) {

    private val catalogAdapter: CatalogAdapter by lazy { CatalogAdapter(::seeAllClick) }

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(ChannelAndCategoriesViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.catalog.observe(this, Observer<Result<Pair<List<Channel>, List<Category>>>> { result ->
            when (result) {
                is Result.Success -> {
                    catalogAdapter.categories = listOf(Category(-1, getString(R.string.channels_you_subscribed), result.data.first)) + result.data.second
                }
                is Result.Failure -> Timber.e(result.e)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh.setOnClickListener {
            vm.onRefreshChannels()
        }
        catalog_list.adapter = catalogAdapter
    }

    private fun seeAllClick(categoryId: Int) {
        router.navigateTo(ChannelKey(categoryId))
    }
}