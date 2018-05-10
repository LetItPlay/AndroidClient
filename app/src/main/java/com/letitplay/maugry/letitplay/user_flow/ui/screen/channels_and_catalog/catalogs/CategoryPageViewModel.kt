package com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.catalogs

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.user_flow.ui.BaseViewModel
import com.letitplay.maugry.letitplay.utils.ext.toLiveData

class CategoryPageViewModel(
        private val channelRepo: ChannelRepository,
        private val schedulerProvider: SchedulerProvider
) : BaseViewModel(), LifecycleObserver {

    val channelId = MutableLiveData<Int>()

    val channels: LiveData<List<Channel>> = Transformations.switchMap(channelId, { channelId ->
        channelRepo
                .channelsFromCategory(channelId)
                .toLiveData()
    })
}