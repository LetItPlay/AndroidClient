package com.letitplay.maugry.letitplay.user_flow.business.channels

import com.letitplay.maugry.letitplay.data_management.manager.ChannelManager
import com.letitplay.maugry.letitplay.data_management.model.*
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.business.ExecutionConfig
import com.letitplay.maugry.letitplay.user_flow.business.Splash.SplashPresenter
import com.letitplay.maugry.letitplay.user_flow.business.trends.TrendsPresenter
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers


object ChannelPresenter : BasePresenter<IMvpView>() {

    var extendChannelList: List<ExtendChannelModel>? = null

    var updatedChannel: ChannelModel? = null

    fun loadChannels(triggerProgress: Boolean = true,
                     onError: ((IMvpView?, Throwable) -> Unit)? = null,
                     onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    triggerProgress = triggerProgress,
                    asyncObservable = ChannelManager.getExtendChannel(),
                    onErrorWithContext = onError,
                    onNextNonContext = {
                        extendChannelList = it.filter {
                            val lang = it.channel?.lang?.let { lang -> ContentLanguage.getLanguage(lang) }
                            return@filter currentContentLang == lang
                        }.sortedByDescending { it.channel?.subscriptionCount }
                    },
                    onCompleteWithContext = onComplete

            )
    )

    fun loadChannelsFromRemote(onError: ((IMvpView?, Throwable) -> Unit)? = null, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = Observable.zip(
                            ChannelManager.getChannels(),
                            ChannelManager.getFollowingChannels(),
                            BiFunction { channels: List<ChannelModel>, followingChannels: List<FollowingChannelModel> ->
                                Pair(channels, followingChannels)
                            })
                            .observeOn(Schedulers.io())
                            .doOnNext { pair ->
                                val extendChannelList: List<ExtendChannelModel> = pair.first.map {
                                    val id = it.id
                                    ExtendChannelModel(it.id, it, pair.second.find { it.id == id }
                                    )
                                }
                                ChannelManager.updateExtendChannel(extendChannelList)
                            },
                    triggerProgress = false,
                    onErrorWithContext = onError,
                    onCompleteWithContext = {
                        loadChannels(false, onError, onComplete)
                    }
            )
    )

    fun updateChannelFollowers(channel: ExtendChannelModel, body: FollowersModel, onComplete: ((IMvpView?) -> Unit)? = null) = execute(
            ExecutionConfig(
                    asyncObservable = ChannelManager.updateChannelFollowers(channel.id!!, body),
                    triggerProgress = false,
                    onNextNonContext = {
                        updatedChannel = it
                        channel.channel?.subscriptionCount = it.subscriptionCount
                        ChannelManager.updateExtendChannel(channel)
                        channel.following?.let{
                            it.isFollowing = !it.isFollowing
                            ChannelManager.updateFollowingChannels(it)
                        }
                    },
                    onCompleteWithContext = onComplete
            )
    )
}