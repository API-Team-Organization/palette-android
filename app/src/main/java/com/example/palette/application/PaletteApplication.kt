package com.example.palette.application

import android.app.Application
import android.content.Context

class PaletteApplication : Application() {

    companion object {
        lateinit var prefs: PreferenceManager

        private lateinit var instance: PaletteApplication

        fun getContext(): Context {
            return instance
        }
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}