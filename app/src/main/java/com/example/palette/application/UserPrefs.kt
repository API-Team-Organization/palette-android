package com.example.palette.application

import android.content.Context
import android.content.SharedPreferences

object UserPrefs {
    private const val PREFS_NAME = "palette_prefs"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_BIRTH_DATE = "user_birth_date"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var userName: String?
        get() = sharedPreferences.getString(KEY_USER_NAME, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_USER_NAME, value).apply()
        }

    var userId: String?
        get() = sharedPreferences.getString(KEY_USER_ID, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_USER_ID, value).apply()
        }

    var userBirthDate: String?
        get() = sharedPreferences.getString(KEY_USER_BIRTH_DATE, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_USER_BIRTH_DATE, value).apply()
        }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}
