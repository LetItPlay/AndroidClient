package com.letitplay.maugry.letitplay.utils.ext

import android.os.Build
import android.text.Html
import android.text.Spanned

fun String.splitTags(): List<String> =
        this.split(",")
                .map(String::trim)
                .filter(String::isNotEmpty)

fun String.isHtml(): Boolean {
    // FIXME ASAP
    val someHtmlTags = listOf("<h1>", "<p>", "<a>", "<b>", "<i>")
    return someHtmlTags.any { this@isHtml.contains(it, true) }
}

fun String.toHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT) else Html.fromHtml(this)
}

fun List<Int>.joinWithComma() = this.joinToString(",")