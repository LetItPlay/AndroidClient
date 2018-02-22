package com.letitplay.maugry.letitplay.utils

import android.content.Context
import android.content.SharedPreferences
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject

class PreferenceHelper(context: Context) {

    private val languageSubject: BehaviorSubject<Optional<Language>> by lazy { BehaviorSubject.createDefault(Optional.of(contentLanguage)) }
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
    }

    var contentLanguage: Language?
        get() {
            val language = sharedPreferences.getString(APP_SETTINGS_CONTENT_LANG, NO_VALUE)
            return if (language != NO_VALUE) Language.valueOf(language) else null
        }
        set(value) {
            if (value != null) {
                sharedPreferences
                        .edit()
                        .putString(APP_SETTINGS_CONTENT_LANG, value.name)
                        .apply()
                languageSubject.onNext(Optional.of(value))
            }
        }

    val liveLanguage: Flowable<Optional<Language>> get() = languageSubject.toFlowable(BackpressureStrategy.DROP)

    fun isListened(trackId: Int): Boolean {
        return sharedPreferences.getBoolean(trackId.toString(), false)
    }

    fun saveListened(trackId: Int) {
        sharedPreferences
                .edit()
                .putBoolean(trackId.toString(), true)
                .apply()
    }

    companion object {
        private const val APP_SETTINGS = "APP_SETTINGS"
        private const val APP_SETTINGS_CONTENT_LANG = "APP_SETTINGS_CONTENT_LANG"
        private const val NO_VALUE = "NO_VALUE"
    }
}