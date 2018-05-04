package com.letitplay.maugry.letitplay.data_management.db.entity

import com.google.gson.annotations.SerializedName

enum class Language(val strValue: String) {
    @SerializedName("ru")
    RU("ru"),
    @SerializedName("en")
    EN("en"),
    @SerializedName("fr")
    FR("fr"),
    @SerializedName("zh")
    ZH("zh");

    companion object {
        fun fromString(strValue: String): Language? {
            return Language.values().firstOrNull { it.strValue == strValue }
        }
    }
}