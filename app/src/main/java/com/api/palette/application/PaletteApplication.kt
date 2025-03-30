package com.api.palette.application

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PaletteApplication : Application() {

    companion object {
        lateinit var prefs: PreferenceManager
        private lateinit var instance: PaletteApplication
        fun getContext(): Context = instance
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        instance = this
        prefs = PreferenceManager(applicationContext)
    }
}
