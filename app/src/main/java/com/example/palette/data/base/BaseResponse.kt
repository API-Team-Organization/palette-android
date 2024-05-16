package com.example.palette.data.base

data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)

data class BaseVoidResponse(
    val code: Int,
    val message: String
)