package com.letitplay.maugry.letitplay.data_management.db

import android.arch.persistence.room.TypeConverter
import com.letitplay.maugry.letitplay.data_management.db.entity.Language


class LanguageConverter {
    @TypeConverter
    fun restoreLanguage(enumOrdinal: Int): Language = Language.values()[enumOrdinal]

    @TypeConverter
    fun saveLanguageToString(enumType: Language): Int = enumType.ordinal
}