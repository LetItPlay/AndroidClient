package com.letitplay.maugry.letitplay.user_flow.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.letitplay.maugry.letitplay.data_management.repo.ChannelRepository
import com.letitplay.maugry.letitplay.data_management.repo.TrendRepository
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.trends.TrendViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
        private val trendRepository: TrendRepository,
        private val channelRepository: ChannelRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrendViewModel::class.java)) {
            return TrendViewModel(trendRepository, channelRepository) as T
        } else if (modelClass.isAssignableFrom(ChannelPageViewModel::class.java)) {
            return ChannelPageViewModel(channelRepository) as T
        }
        throw IllegalArgumentException("Unknown type of view model")
    }
}