package com.letitplay.maugry.letitplay.data_management.db

import android.arch.persistence.room.TypeConverter
import com.letitplay.maugry.letitplay.utils.ext.splitTags


class TagsConverter {
    @TypeConverter
    fun restoreTagList(tags: String): List<String> = tags.splitTags()

    @TypeConverter
    fun saveTagsToString(tags: List<String>?): String = tags?.joinToString() ?: ""
}