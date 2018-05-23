package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.ServiceLocator.router
import com.letitplay.maugry.letitplay.data_management.db.entity.Category
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.ChannelListType
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.ChannelAndCategoriesViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels.ChannelKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels.ChannelPageKey
import kotlinx.android.synthetic.main.catalogs_fragment.*

class CatalogFragment : BaseFragment(R.layout.catalogs_fragment) {

    private val catalogAdapter: CatalogAdapter by lazy { CatalogAdapter(::seeAllClick, ::onChannelClick) }

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(ChannelAndCategoriesViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(vm)
        vm.catalog.observe(this, Observer<Pair<List<Channel>, List<Category>>> {
            it?.let {
                catalogAdapter.categories = listOf(Category(ChannelListType.FAVOURITE, getString(R.string.channels_you_subscribed), it.first)) + it.second
            }
        })
        vm.isLoading.observe(this, Observer<Boolean> {
            when (it) {
                true -> showProgress()
                else -> hideProgress()
            }
        })
        vm.refreshing.observe(this, Observer<Boolean> {
            it?.let {
                swipe_refresh.isRefreshing = it
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.setOnRefreshListener {
            vm.onRefreshChannels()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        catalog_list.adapter = catalogAdapter
    }

    private fun seeAllClick(categoryId: Int) {
        router.navigateTo(ChannelKey(categoryId))
    }

    private fun onChannelClick(channelId: Int) {
        if (swipe_refresh.isRefreshing) return
        router.navigateTo(ChannelPageKey(channelId))
    }
}