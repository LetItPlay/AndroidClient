package com.letitplay.maugry.letitplay.user_flow.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.repo.*
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.profile.ProfileViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.CompilationViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.trends.TrendViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
        private val trendRepository: TrendRepository,
        private val channelRepository: ChannelRepository,
        private val trackRepository: TrackRepository,
        private val feedRepository: FeedRepository,
        private val profileRepository: ProfileRepository,
        private val playerRepository: PlayerRepository,
        private val compilationRepository: CompilationRepository,
        private val schedulerProvider: SchedulerProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TrendViewModel::class.java) ->
                TrendViewModel(
                        trendRepository,
                        channelRepository,
                        trackRepository,
                        playerRepository,
                        schedulerProvider
                )
            modelClass.isAssignableFrom(ChannelPageViewModel::class.java) ->
                ChannelPageViewModel(
                        channelRepository,
                        schedulerProvider
                )
            modelClass.isAssignableFrom(ChannelViewModel::class.java) ->
                ChannelViewModel(
                        channelRepository,
                        schedulerProvider
                )
            modelClass.isAssignableFrom(FeedViewModel::class.java) ->
                FeedViewModel(
                        feedRepository,
                        trackRepository,
                        playerRepository
                )
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(
                        profileRepository,
                        schedulerProvider
                )
            modelClass.isAssignableFrom(CompilationViewModel::class.java) ->
                CompilationViewModel(
                        compilationRepository
                )
            else -> throw IllegalArgumentException("Unknown type of view model")
        } as T
    }
}