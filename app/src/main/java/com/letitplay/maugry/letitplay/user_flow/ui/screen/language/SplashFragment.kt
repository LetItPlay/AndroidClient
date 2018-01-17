package com.letitplay.maugry.letitplay.user_flow.ui.screen.language

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.business.language.SplashPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity


class SplashFragment : BaseFragment<SplashPresenter>(R.layout.language_fragment, SplashPresenter) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.loadData {
            context?.startActivity(Intent(context, NavigationActivity::class.java))
        }
    }
}