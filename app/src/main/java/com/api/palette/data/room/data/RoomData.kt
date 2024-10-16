package com.api.palette.data.room.data

import kotlinx.serialization.Serializable

@Serializable
data class RoomData(
    val id: Int,
    val title: String? = "New Chat",
    val message: String? = "Palette 어시스턴스"
)
