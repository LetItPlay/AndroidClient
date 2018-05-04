package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalogs.catalogs

import android.os.Bundle
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import kotlinx.android.synthetic.main.catalogs_fragment.*

class CatalogsFragment : BaseFragment(R.layout.catalogs_fragment) {

    private val catalogAdapter: CatalogsAdapter by lazy { CatalogsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        catalog_list.adapter = catalogAdapter
    }
}