package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalogs

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.ChannelWithFollow
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.ChannelAndCategoriesViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels.ChannelAdapter
import com.letitplay.maugry.letitplay.utils.Result
import kotlinx.android.synthetic.main.category_page_fragments.*
import timber.log.Timber

class CategoryPageFragment : BaseFragment(R.layout.category_page_fragments) {

    private val channelsListAdapter = ChannelAdapter(::onChannelClick, ::onFollowClick)
    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(ChannelAndCategoriesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.categorylId = getKey()
        vm.channelFromCategory.observe(this, Observer<Result<List<Channel>>> { result ->
            when (result) {
                is Result.Success -> {
                    channelsListAdapter.updateChannels(result.data)
                }
                is Result.Failure -> Timber.e(result.e)
            }
        })
    }
}