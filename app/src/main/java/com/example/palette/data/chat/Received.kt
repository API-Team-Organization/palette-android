package com.example.palette.data.chat

data class Received(
    val id: Int,
    val message: String,
    val datetime: String,
    val roomId: Int,
    val userId: Int,
    val isAi: Boolean,
    val resource: String
)