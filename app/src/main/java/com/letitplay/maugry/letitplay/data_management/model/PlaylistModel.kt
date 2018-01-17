package com.letitplay.maugry.letitplay.data_management.model

import com.gsfoxpro.musicservice.model.AudioTrack


class PlaylistModel(
        val name: String,
        val description: String,
        val tracks: List<AudioTrack>
)