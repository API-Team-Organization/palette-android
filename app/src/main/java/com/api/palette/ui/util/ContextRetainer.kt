package com.api.palette.ui.util

import android.content.Context

object ContextRetainer {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    fun getContext(): Context {
        return applicationContext
    }
}