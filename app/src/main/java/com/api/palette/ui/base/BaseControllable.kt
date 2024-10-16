package com.api.palette.ui.base

import android.content.Context
import com.api.palette.application.PaletteApplication

interface BaseControllable {
    fun bottomVisible(visibility: Boolean)
    fun sessionDialog(context: Context)
    fun deleteRoom(token: String = PaletteApplication.prefs.token, roomId: Int)
}