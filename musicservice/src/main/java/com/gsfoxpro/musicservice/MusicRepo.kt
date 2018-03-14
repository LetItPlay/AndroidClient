package com.gsfoxpro.musicservice

import com.gsfoxpro.musicservice.model.AudioTrack

open class MusicRepo(val playlist: MutableList<AudioTrack>, val isUserMode:Boolean = false) {

    val hasNext get() = !playlist.isEmpty() && currentTrackIndex < playlist.size - 1
    val hasPrev get() = !playlist.isEmpty() && currentTrackIndex > 0

    private var currentTrackIndex = 0

    open var autoPlay = true

    open val currentAudioTrack: AudioTrack?
        get() = getAudioTrackAtIndex(currentTrackIndex)

    open val nextAudioTrack: AudioTrack?
        get() = when (hasNext) {
            true -> getAudioTrackAtIndex(++currentTrackIndex)
            else -> null
        }

    open val prevAudioTrack: AudioTrack?
        get() = when (hasPrev) {
            true -> getAudioTrackAtIndex(--currentTrackIndex)
            else -> null
        }

    open fun addTrackToStart(track:AudioTrack){
        if (isUserMode) {
            playlist.add(0, track)
        }
    }
    open fun addTrackToEnd(track:AudioTrack){
        if (isUserMode) {
            playlist.add(playlist.size, track)
        }
    }
    open fun removeTrack(id:Int){
        if (isUserMode) {
            playlist.removeAt(id)
        }
    }

    open fun getAudioTrackAtId(id: Int): AudioTrack? {
        val index =  playlist.indexOfFirst { it.id == id }
        if (index != -1) {
            currentTrackIndex = index
        }
        return getAudioTrackAtIndex(index)
    }

    private fun getAudioTrackAtIndex(index: Int): AudioTrack? = playlist.getOrNull(index)
}