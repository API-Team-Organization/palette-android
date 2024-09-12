package com.example.palette.data.chat

data class GetImageListResponse(
    val code: Int,
    val message: String,
    val data: List<String>
)
