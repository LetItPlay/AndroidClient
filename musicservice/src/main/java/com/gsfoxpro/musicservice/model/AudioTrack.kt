package com.gsfoxpro.musicservice.model

import java.util.*

open class AudioTrack(
        open val id: Int,
        open val url: String,
        open val title: String? = null,
        open val subtitle: String? = null,
        open val imageUrl: String? = null,
        open val channelTitle: String? = null,
        open val lengthInMs: Long? = 0,
        open val listenCount: Int? = 0,
        open val publishedAt: Date? = null,
        open val description:String? = null,
        open val likeCount: Int? = null,
        open val isLiked: Int? = null
) {
    val lengthInSeconds = (lengthInMs?.div(1000) ?: 0).toInt()
}