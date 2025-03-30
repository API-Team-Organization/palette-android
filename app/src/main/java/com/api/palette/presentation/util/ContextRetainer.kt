package com.api.palette.presentation.util

import android.content.Context

object ContextRetainer {

    private var _applicationContext: Context? = null

    val context: Context
        get() = _applicationContext
            ?: throw IllegalStateException("ContextRetainer is not initialized. Call init(context) first.")

    fun init(context: Context) {
        _applicationContext = context.applicationContext
    }
}
