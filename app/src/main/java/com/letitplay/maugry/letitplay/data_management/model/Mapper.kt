package com.letitplay.maugry.letitplay.data_management.model

import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.data_management.api.responses.FeedResponseItem
import com.letitplay.maugry.letitplay.data_management.api.responses.TracksAndChannels
import com.letitplay.maugry.letitplay.data_management.api.responses.UpdatedChannelResponse
import com.letitplay.maugry.letitplay.data_management.api.responses.UpdatedTrackResponse
import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Like
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.utils.ext.splitTags

fun toChannelModel(updatedChannelResponse: UpdatedChannelResponse): Channel {
    return Channel(
            id = updatedChannelResponse.id,
            lang = updatedChannelResponse.lang,
            name = updatedChannelResponse.name,
            imageUrl = updatedChannelResponse.imageUrl.fixMediaPrefix(),
            subscriptionCount = updatedChannelResponse.subscriptionCount,
            tags = updatedChannelResponse.tags?.splitTags()
    )
}

fun toTrackModel(updatedTrackResponse: UpdatedTrackResponse): Track {
    return Track(
            id = updatedTrackResponse.id,
            lang = updatedTrackResponse.lang,
            stationId = updatedTrackResponse.stationId,
            title = updatedTrackResponse.name,
            description = updatedTrackResponse.description,
            coverUrl = updatedTrackResponse.imageUrl?.fixMediaPrefix(),
            audioUrl = updatedTrackResponse.audioFile.filePath.fixMediaPrefix(),
            totalLengthInSeconds = updatedTrackResponse.audioFile.lengthInSeconds,
            likeCount = updatedTrackResponse.likeCount,
            tags = updatedTrackResponse.tags?.splitTags(),
            listenCount = updatedTrackResponse.listenCount,
            publishedAt = updatedTrackResponse.publishedAt
    )
}

fun toTrackWithChannels(tracks: List<Track>, channels: List<Channel>): List<TrackWithChannel> {
    val channelHashMap = channels.associateBy(Channel::id)
    return tracks.map {
        TrackWithChannel(it, channelHashMap[it.stationId]!!, null)
    }
}

fun toTrackWithChannels(tracks: List<Track>, channels: List<Channel>, likes: List<Like>): List<TrackWithChannel> {
    val likesHashMap = likes.associateBy(Like::trackId)
    val channelHashMap = channels.associateBy(Channel::id)
    return tracks.map {
        TrackWithChannel(it, channelHashMap[it.stationId]!!, likesHashMap[it.id]?.trackId)
    }
}

fun toTrackWithChannels(tracksAndChannels: TracksAndChannels): List<TrackWithChannel> {
    return with(tracksAndChannels) {
        if (tracks != null && channels != null)
            toTrackWithChannels(tracks, channels)
        else
            emptyList()
    }
}

fun toTrackWithChannel(feedItem: FeedResponseItem): TrackWithChannel {
    return TrackWithChannel(
            Track(
                    id = feedItem.id,
                    lang = feedItem.lang,
                    stationId = feedItem.channel.id,
                    title = feedItem.title,
                    description = feedItem.description,
                    coverUrl = feedItem.coverUrl,
                    audioUrl = feedItem.audioUrl,
                    totalLengthInSeconds = feedItem.totalLengthInSeconds,
                    likeCount = feedItem.likeCount,
                    tags = feedItem.tags,
                    listenCount = feedItem.listenCount,
                    publishedAt = feedItem.publishedDate),
            channel = feedItem.channel,
            likeId = null
    )
}

fun feedItemToTrackWithChannels(feedItems: List<FeedResponseItem>): List<TrackWithChannel> {
    return feedItems.map(::toTrackWithChannel)
}

fun feedToTrackWithChannels(feedItems: List<FeedResponseItem>, likes: List<Like>): List<TrackWithChannel> {
    val likesHashMap = likes.associateBy(Like::trackId)
    return feedItemToTrackWithChannels(feedItems).map { it.copy(likeId = likesHashMap[it.track.id]?.trackId) }
}

fun String.fixMediaPrefix(): String =
        when {
            this.startsWith("http") -> this
            else -> GL_MEDIA_SERVICE_URL + this
        }

fun TrackWithChannel.toAudioTrack(): AudioTrack {
    return AudioTrack(
            id = track.id,
            url = track.audioUrl ?: "",
            title = track.title,
            subtitle = channel.name,
            imageUrl = track.coverUrl ?: "",
            channelTitle = channel.name,
            lengthInMs = (track.totalLengthInSeconds * 1000).toLong(),
            listenCount = track.listenCount,
            publishedAt = track.publishedAt
    )
}