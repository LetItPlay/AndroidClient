package com.letitplay.maugry.letitplay.data_management.model

enum class ContentLanguage {
    RU,
    EN,
    UNKNOWN;

    companion object {
        fun getLanguage(language: String): ContentLanguage {
            return ContentLanguage.values().firstOrNull { it.name == language.toUpperCase() } ?: UNKNOWN
        }
    }
}