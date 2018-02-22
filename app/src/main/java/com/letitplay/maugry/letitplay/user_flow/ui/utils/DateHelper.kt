package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.content.Context
import com.letitplay.maugry.letitplay.R
import org.joda.time.*
import java.util.*


object DateHelper {

    data class PassedDateTime(val weeks: Int, val days: Int, val hours: Int, val minutes: Int, val seconds: Int)


    private fun getPastComponents(date: Date): PassedDateTime {
        val now = DateTime.now()
        val publish = DateTime(date)
        return PassedDateTime(
                weeks = Weeks.weeksBetween(publish, now).weeks,
                days = Days.daysBetween(publish, now).days,
                hours = Hours.hoursBetween(publish, now).hours,
                minutes = Minutes.minutesBetween(publish, now).minutes,
                seconds = Seconds.secondsBetween(publish, now).seconds
        )
    }

    fun getTime(timeInSeconds: Int?): String {
        var secondsStr = "00"
        var minutesStr = "00"
        timeInSeconds?.let {
            val seconds = it % 60
            val minutes = (timeInSeconds / 60) % 60
            val hours = timeInSeconds / 60 / 60

            secondsStr = "${if (seconds < 10) "0" else ""}$seconds"
            minutesStr = "${if (minutes < 10) "0" else ""}$minutes"

            if (hours > 0) {
                return "$hours:$minutesStr:$secondsStr"
            }
        }
        return "$minutesStr:$secondsStr"
    }

    fun getLongPastDate(eventDay: Date?, ctx: Context): String {
        if (eventDay == null) return ""
        val passed = getPastComponents(eventDay)
        val (component, resource) = when {
            passed.weeks != 0 -> passed.weeks to R.plurals.weeks_long
            passed.days != 0 -> passed.days to R.plurals.days_long
            passed.hours != 0 -> passed.hours to R.plurals.hours_long
            passed.minutes != 0 -> passed.minutes to R.plurals.minutes_long
            else -> passed.seconds to R.plurals.seconds_long
        }
        return ctx.resources.getQuantityString(resource, component, component)
    }

    fun getShortPastDate(eventDay: Date?, ctx: Context): String {
        if (eventDay == null) return ""
        val passed = getPastComponents(eventDay)
        val (component, resource) = when {
            passed.weeks != 0 -> passed.weeks to R.string.weeks_short
            passed.days != 0 -> passed.days to R.string.days_short
            passed.hours != 0 -> passed.hours to R.string.hours_short
            passed.minutes != 0 -> passed.minutes to R.string.minutes_short
            else -> passed.seconds to R.string.seconds_short
        }
        return ctx.resources.getString(resource, component)
    }
}