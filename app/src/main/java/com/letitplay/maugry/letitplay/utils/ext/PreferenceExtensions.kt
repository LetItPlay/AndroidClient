package com.letitplay.maugry.letitplay.utils.ext

import android.content.SharedPreferences


inline fun SharedPreferences.transaction(crossinline block: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.apply(block)
    editor.apply()
}