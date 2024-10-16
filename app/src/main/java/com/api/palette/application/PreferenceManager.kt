package com.api.palette.application

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PALETTE_APP, Context.MODE_PRIVATE)

    var token: String by PreferenceDelegate("TOKEN", "")
    var isFirst: Boolean by PreferenceDelegate("IS_FIRST", true)
    var username: String by PreferenceDelegate("USERNAME", "")
    var userId: String by PreferenceDelegate("USER_ID", "")
    var userBirthDate: String by PreferenceDelegate("USER_BIRTH_DATE", "")

    fun clearToken() {
        token = ""
    }

    fun clearUser() {
        username = ""
        userId = ""
        userBirthDate = ""
    }

    companion object {
        private const val PALETTE_APP = "PALETTE_APP"
    }

    private inner class PreferenceDelegate<T>(
        private val key: String,
        private val defaultValue: T
    ) {
        operator fun getValue(thisRef: Any?, property: Any?): T {
            return when (defaultValue) {
                is String -> prefs.getString(key, defaultValue as String) as T
                is Boolean -> prefs.getBoolean(key, defaultValue as Boolean) as T
                is Int -> prefs.getInt(key, defaultValue as Int) as T
                else -> throw IllegalArgumentException("Unsupported preference type")
            }
        }

        operator fun setValue(thisRef: Any?, property: Any?, value: T) {
            when (value) {
                is String -> prefs.edit().putString(key, value as String).apply()
                is Boolean -> prefs.edit().putBoolean(key, value as Boolean).apply()
                is Int -> prefs.edit().putInt(key, value as Int).apply()
                else -> throw IllegalArgumentException("Unsupported preference type")
            }
        }
    }
}