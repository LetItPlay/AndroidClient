package com.letitplay.maugry.letitplay.data_management.db

import android.arch.persistence.room.TypeConverter
import java.util.*


class DateConverter {
    @TypeConverter
    fun restoreDate(value: Long) = Date(value)

    @TypeConverter
    fun saveDate(date: Date) = date.time
}