package com.letitplay.maugry.letitplay.data_management.model

data class PlaybackSpeed(val value: Float)

val DEFAULT_PLAYBACK_SPEED = PlaybackSpeed(1.0f)

fun availableSpeeds(): List<PlaybackSpeed> {
    val values = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2f)
    return values.map(::PlaybackSpeed)
}