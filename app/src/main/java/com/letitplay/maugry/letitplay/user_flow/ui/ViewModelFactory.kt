package com.letitplay.maugry.letitplay.user_flow.ui

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.repo.channel.ChannelRepository
import com.letitplay.maugry.letitplay.data_management.repo.compilation.CompilationRepository
import com.letitplay.maugry.letitplay.data_management.repo.feed.FeedRepository
import com.letitplay.maugry.letitplay.data_management.repo.player.PlayerRepository
import com.letitplay.maugry.letitplay.data_management.repo.playlists.PlaylistsRepository
import com.letitplay.maugry.letitplay.data_management.repo.profile.ProfileRepository
import com.letitplay.maugry.letitplay.data_management.repo.search.SearchRepository
import com.letitplay.maugry.letitplay.data_management.repo.track.TrackRepository
import com.letitplay.maugry.letitplay.data_management.repo.trend.TrendRepository
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.ChannelAndCategoriesViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels.ChannelPageViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.global.PlayerViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists.PlaylistsViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.profile.ProfileViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.compilation.CompilationViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query.SearchViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.trends.TrendViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
        private val application: Application,
        private val trendRepository: TrendRepository,
        private val channelRepository: ChannelRepository,
        private val trackRepository: TrackRepository,
        private val feedRepository: FeedRepository,
        private val profileRepository: ProfileRepository,
        private val playlistRepository: PlaylistsRepository,
        private val playerRepository: PlayerRepository,
        private val compilationRepository: CompilationRepository,
        private val searchRepository: SearchRepository,
        private val schedulerProvider: SchedulerProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(PlayerViewModel::class.java) ->
                    PlayerViewModel(
                            application,
                            trackRepository,
                            channelRepository
                    )
                isAssignableFrom(TrendViewModel::class.java) ->
                    TrendViewModel(
                            trendRepository,
                            channelRepository,
                            trackRepository,
                            playerRepository,
                            schedulerProvider
                    )
                isAssignableFrom(ChannelPageViewModel::class.java) ->
                    ChannelPageViewModel(
                            channelRepository,
                            schedulerProvider
                    )
                isAssignableFrom(ChannelAndCategoriesViewModel::class.java) ->
                    ChannelAndCategoriesViewModel(
                            channelRepository,
                            schedulerProvider
                    )
                isAssignableFrom(FeedViewModel::class.java) ->
                    FeedViewModel(
                            feedRepository,
                            trackRepository,
                            channelRepository,
                            playerRepository
                    )
                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(
                            trackRepository,
                            profileRepository,
                            schedulerProvider
                    )
                isAssignableFrom(PlaylistsViewModel::class.java) ->
                    PlaylistsViewModel(
                            playlistRepository,
                            trackRepository,
                            schedulerProvider
                    )
                isAssignableFrom(CompilationViewModel::class.java) ->
                    CompilationViewModel(
                            compilationRepository
                    )
                isAssignableFrom(SearchViewModel::class.java) ->
                    SearchViewModel(
                            trackRepository,
                            searchRepository,
                            channelRepository,
                            playerRepository
                    )
                else -> throw IllegalArgumentException("Unknown type of view model")
            } as T
        }
    }
}