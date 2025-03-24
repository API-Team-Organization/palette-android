package com.api.palette.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.api.palette.data.ApiClient
import com.api.palette.data.repository.AppRepository
import com.api.palette.data.repository.RepositoryProvider

class PaletteApplication : Application() {

    companion object {
        lateinit var prefs: PreferenceManager
        lateinit var appRepository: AppRepository
        lateinit var instance: PaletteApplication

        fun getContext() = instance
    }

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        instance = this
        prefs = PreferenceManager(applicationContext)

        val retrofit = ApiClient.retrofit
        appRepository = RepositoryProvider.provideAppRepository(retrofit)
    }
}
