package com.example.palette.data.chat

import kotlinx.serialization.Serializable

@Serializable
data class Received(
    val id: Int?,
    val message: String,
    val datetime: String,
    val roomId: Int,
    val userId: Int,
    val isAi: Boolean,
    val resource: String,
    val promptId: String?,
)