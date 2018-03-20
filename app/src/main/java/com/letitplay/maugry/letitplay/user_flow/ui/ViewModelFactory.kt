package com.letitplay.maugry.letitplay.user_flow.ui

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
import com.letitplay.maugry.letitplay.data_management.repo.userToken.UserTokenDataRepository
import com.letitplay.maugry.letitplay.data_management.repo.userToken.UserTokenRepository
import com.letitplay.maugry.letitplay.user_flow.ui.screen.JwtViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelPageViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels.ChannelViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.feed.FeedViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.playlists.PlaylistsViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.profile.ProfileViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.compilation.CompilationViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query.SearchViewModel
import com.letitplay.maugry.letitplay.user_flow.ui.screen.trends.TrendViewModel


@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
        private val trendRepository: TrendRepository,
        private val channelRepository: ChannelRepository,
        private val trackRepository: TrackRepository,
        private val feedRepository: FeedRepository,
        private val profileRepository: ProfileRepository,
        private val playlistRepository: PlaylistsRepository,
        private val playerRepository: PlayerRepository,
        private val compilationRepository: CompilationRepository,
        private val userTokenRepository: UserTokenRepository,
        private val searchRepository: SearchRepository,
        private val schedulerProvider: SchedulerProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
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
                isAssignableFrom(ChannelViewModel::class.java) ->
                    ChannelViewModel(
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
                            profileRepository,
                            schedulerProvider
                    )
                isAssignableFrom(PlaylistsViewModel::class.java) ->
                    PlaylistsViewModel(
                            playlistRepository,
                            schedulerProvider
                    )
                isAssignableFrom(CompilationViewModel::class.java) ->
                    CompilationViewModel(
                            compilationRepository
                    )
                isAssignableFrom(JwtViewModel::class.java) ->
                    JwtViewModel(
                            userTokenRepository
                    )
                isAssignableFrom(SearchViewModel::class.java) ->
                    SearchViewModel(
                            searchRepository,
                            channelRepository,
                            playerRepository
                    )
                else -> throw IllegalArgumentException("Unknown type of view model")
            } as T
        }
    }
}