package com.letitplay.maugry.letitplay.data_management.model

import com.letitplay.maugry.letitplay.data_management.db.entity.Channel
import com.letitplay.maugry.letitplay.data_management.db.entity.Track


class CompilationModel(
        val title: String,
        val description: String,
        val tracks: List<Track>,
        val channels: List<Channel>
)