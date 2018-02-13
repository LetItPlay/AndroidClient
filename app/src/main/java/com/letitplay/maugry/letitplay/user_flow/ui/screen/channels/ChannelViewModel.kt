package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels

import android.arch.lifecycle.ViewModel
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.repo.ChannelRepository
import io.reactivex.Flowable


class ChannelViewModel(
        private val channelRepo: ChannelRepository
): ViewModel() {

    val channels: Flowable<List<Channel>> = channelRepo.channels()
}