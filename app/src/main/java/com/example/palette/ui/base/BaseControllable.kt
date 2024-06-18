package com.example.palette.ui.base

import android.content.Context

interface BaseControllable {
    fun bottomVisible(visibility: Boolean)
    fun sessionDialog(context: Context)
}