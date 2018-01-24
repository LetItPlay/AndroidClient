package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.content.Context
import com.letitplay.maugry.letitplay.R
import java.util.*


object DateHelper {

    data class PassedDateTime(val years: Int, val months: Int, val days: Int, val hours: Int, val minutes: Int, val seconds: Int)


    private fun getPastComponents(date: Date): PassedDateTime {
        val now = System.currentTimeMillis()
        val diff = Date(now - date.time)
        val cal = Calendar.getInstance()
        cal.time = diff
        return PassedDateTime(
                years = cal.get(Calendar.YEAR) - 1970,
                months = cal.get(Calendar.MONTH),
                days = cal.get(Calendar.DAY_OF_MONTH) - 1,
                hours = cal.get(Calendar.HOUR),
                minutes = cal.get(Calendar.MINUTE),
                seconds = cal.get(Calendar.SECOND)
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
            passed.years != 0 -> passed.years to R.plurals.years_long
            passed.months != 0 -> passed.months to R.plurals.months_long
            passed.days != 0 -> passed.days to R.plurals.days_long
            passed.hours != 0 -> passed.hours to R.plurals.hours_long
            passed.minutes != 0 -> passed.minutes to R.plurals.months_long
            else -> passed.seconds to R.plurals.seconds_long
        }
        return ctx.resources.getQuantityString(resource, component, component)
    }

    fun getShortPastDate(eventDay: Date?, ctx: Context): String {
        if (eventDay == null) return ""
        val passed = getPastComponents(eventDay)
        val (component, resource) = when {
            passed.years != 0 -> passed.years to R.string.years_short
            passed.months != 0 -> passed.months to R.string.months_short
            passed.days != 0 -> passed.days to R.string.days_short
            passed.hours != 0 -> passed.hours to R.string.hours_short
            passed.minutes != 0 -> passed.minutes to R.string.months_short
            else -> passed.seconds to R.string.seconds_short
        }
        return ctx.resources.getString(resource).format(component)
    }
}