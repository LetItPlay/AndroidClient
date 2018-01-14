package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.content.Context
import com.letitplay.maugry.letitplay.R
import java.util.*


object DataHelper {

    fun getTime(timeInSeconds: Int?): String {
        var seconds = 0
        var minutes = 0
        var hours = 0
        timeInSeconds?.let {
            val cal = Calendar.getInstance()
            cal.time = Date(timeInSeconds.toLong() * 1000)
            seconds = cal.get(Calendar.SECOND)
            minutes = cal.get(Calendar.MINUTE)
            hours = cal.get(Calendar.HOUR)
        }
        return hours.toString() + ":" + minutes.toString() + ":" + seconds.toString()
    }

    fun getData(date: Date?, ctx: Context): String {
        date?.let {
            val now = System.currentTimeMillis()
            val diff = Date(now - date.time)
            val cal = Calendar.getInstance()
            cal.time = diff
            val seconds = cal.get(Calendar.SECOND)
            val minutes = cal.get(Calendar.MINUTE)
            val hours = cal.get(Calendar.HOUR)
            val days = cal.get(Calendar.DAY_OF_MONTH)
            val months = cal.get(Calendar.MONTH)
            val years = cal.get(Calendar.YEAR) - 1970
            if (years != 0) return years.toString() + " " + ctx.resources.getString(R.string.feed_years)
            if (months != 0) return months.toString() + " " + ctx.resources.getString(R.string.feed_months)
            if (days != 0) return days.toString() + " " + ctx.resources.getString(R.string.feed_days)
            if (hours != 0) return hours.toString() + " " + ctx.resources.getString(R.string.feed_years)
            if (minutes != 0) return minutes.toString() + " " + ctx.resources.getString(R.string.feed_years)
            else return seconds.toString()
        }
        return ""
    }
}