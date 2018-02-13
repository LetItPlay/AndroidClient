package com.letitplay.maugry.letitplay.data_management.model

import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import java.util.*

data class Track(
        val id: Int,
        val lang: Language,
        val title: String,
        val description: String? = null,
        val coverUrl: String? = null,
        val audioUrl: String? = null,
        val totalLengthInSeconds: Int,
        val likeCount: Int,
        val tags: List<String>? = null,
        val listenCount: Int,
        val publishedAt: Date,
        val channel: Channel
)