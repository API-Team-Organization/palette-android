package com.api.palette.presentation.base

import android.content.Context

interface BaseControllable {
    fun bottomVisible(visibility: Boolean)
    fun sessionDialog(context: Context)
    fun deleteRoom(token: String, roomId: String)
}
