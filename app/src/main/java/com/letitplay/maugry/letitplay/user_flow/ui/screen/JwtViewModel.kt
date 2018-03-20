package com.letitplay.maugry.letitplay.user_flow.ui.screen

import android.arch.lifecycle.LifecycleObserver
import com.letitplay.maugry.letitplay.data_management.repo.userToken.UserTokenRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel


class JwtViewModel(
        val userTokenRepository: UserTokenRepository
) : BaseViewModel(), LifecycleObserver {

    var jwt: String? = null

}