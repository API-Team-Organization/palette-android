package com.example.palette.data.room.data

import kotlinx.serialization.Serializable

@Serializable
data class RoomData(
    val id: Int,
    val title: String? = "제목이 없습니다."
)
