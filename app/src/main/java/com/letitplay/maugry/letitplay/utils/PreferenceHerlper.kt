package com.letitplay.maugry.letitplay.utils

import android.content.Context
import android.content.SharedPreferences
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage

class PreferenceHerlper(context: Context) {

    companion object {
        private const val APP_SETTINGS = "APP_SETTINGS"
        private const val APP_SETTINGS_CONTENT_LANG = "APP_SETTINGS_CONTENT_LANG"
    }

    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
    }

    var contentLanguage: ContentLanguage
        get() {
            val language = sharedPreferences.getString(APP_SETTINGS_CONTENT_LANG, ContentLanguage.RU.name)
            return ContentLanguage.getLanguage(language)
        }
        set(value) {
            sharedPreferences
                    .edit()
                    .putString(APP_SETTINGS_CONTENT_LANG, value.name)
                    .apply()
        }
}