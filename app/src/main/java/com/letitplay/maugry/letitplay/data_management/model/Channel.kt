package com.letitplay.maugry.letitplay.data_management.model

import com.letitplay.maugry.letitplay.data_management.db.entity.Language


data class Channel(
        val id: Int,
        val lang: Language,
        val name: String,
        val imageUrl: String,
        val subscriptionCount: Int,
        val tags: List<String>?
)