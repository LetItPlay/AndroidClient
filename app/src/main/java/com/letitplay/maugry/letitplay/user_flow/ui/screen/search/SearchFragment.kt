package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.user_flow.business.search.SearchAdapter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.search_fragment.*


class SearchFragment : BaseFragment(R.layout.search_fragment) {

    private val searchAdapter = SearchAdapter()


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_list.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
        }
        searchAdapter.setData(arrayListOf(ChannelModel(), ChannelModel(), ChannelModel(), ChannelModel()))
    }

    private fun goToOtherView() {
    }

}