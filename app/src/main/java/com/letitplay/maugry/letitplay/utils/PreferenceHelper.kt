package com.letitplay.maugry.letitplay.utils

import android.content.Context
import android.content.SharedPreferences
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.model.DEFAULT_PLAYBACK_SPEED
import com.letitplay.maugry.letitplay.data_management.model.PlaybackSpeed
import com.letitplay.maugry.letitplay.utils.ext.transaction
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
                putString(APP_SETTINGS_CONTENT_LANG, value.name)
                languageSubject.onNext(Optional.of(value))
            }
        }

    var isAdult: Boolean
        get() {
            return sharedPreferences.getBoolean(APP_SETTINGS_IS_ADULT, false)
        }
        set(value) {
            sharedPreferences
                    .edit()
                    .putBoolean(APP_SETTINGS_IS_ADULT, value)
                    .apply()
        }

    var userName: String
        get() {
            return sharedPreferences.getString(APP_SETTINGS_USER_NAME, DEFAULT_USER_NAME)
        }
        set(value) {
            sharedPreferences
                    .edit()
                    .putString(APP_SETTINGS_USER_NAME, value)
                    .apply()

        }

    var userToken: String
        get() {
            return sharedPreferences.getString(APP_SETTINGS_USER_TOKEN, NO_VALUE)
        }
        set(value) {
            sharedPreferences
                    .edit()
                    .putString(APP_SETTINGS_USER_TOKEN, value)
                    .apply()
        }

    var userJwt: String
        get() {
            return sharedPreferences.getString(APP_SETTINGS_USER_JWT, NO_VALUE)
        }
        set(value) {
            sharedPreferences
                    .edit()
                    .putString(APP_SETTINGS_USER_JWT, value)
                    .apply()
        }

    var playbackSpeed: PlaybackSpeed
        get() = PlaybackSpeed(sharedPreferences.getFloat(PLAYBACK_SPEED, DEFAULT_PLAYBACK_SPEED.value))
        set(value) {
            putFloat(PLAYBACK_SPEED, value.value)
        }

    val liveLanguage: Flowable<Optional<Language>> get() = languageSubject.toFlowable(BackpressureStrategy.DROP)

    fun isListened(trackId: Int): Boolean {
        return sharedPreferences.getBoolean(trackId.toString(), false)
    }

    fun saveListened(trackId: Int) {
        sharedPreferences.transaction {
            putBoolean(trackId.toString(), true)
        }
    }

    private fun putString(key: String, value: String) {
        sharedPreferences.transaction {
            putString(key, value)
        }
    }

    private fun putFloat(key: String, value: Float) {
        sharedPreferences.transaction {
            putFloat(key, value)
        }
    }

    companion object {
        private const val APP_SETTINGS = "APP_SETTINGS"
        private const val APP_SETTINGS_CONTENT_LANG = "APP_SETTINGS_CONTENT_LANG"
        private const val APP_SETTINGS_USER_TOKEN = "APP_SETTINGS_USER_TOKEN"
        private const val APP_SETTINGS_USER_JWT = "APP_SETTINGS_USER_JWT"
        private const val APP_SETTINGS_USER_NAME = "APP_SETTINGS_USER_NAME"
        private const val APP_SETTINGS_IS_ADULT = "APP_SETTINGS_IS_ADULT"
        const val DEFAULT_USER_NAME = "default user"
        const val NO_VALUE = "NO_VALUE"
        const val PLAYBACK_SPEED = "APP_PLAYBACK_SPEED"
        const val PROFILE_FILENAME = "user_avatar.jpg"
    }
}