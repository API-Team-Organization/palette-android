package com.example.palette.ui.base

import android.content.Context
import com.example.palette.application.PaletteApplication

interface BaseControllable {
    fun bottomVisible(visibility: Boolean)
    fun sessionDialog(context: Context)
    fun deleteRoom(token: String = PaletteApplication.prefs.token, roomId: Int)
}