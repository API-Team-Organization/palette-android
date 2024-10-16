package com.api.palette.data.base

import kotlinx.serialization.Serializable

@Serializable
sealed interface BaseResponse {
    val code: Int
    val message: String
}

@Serializable
data class DataResponse<T>(
    override val code: Int,
    override val message: String,
    val data: T,
) : BaseResponse

@Serializable
data class VoidResponse(
    override val code: Int,
    override val message: String
) : BaseResponse

@Serializable
data class ErrorResponse(
    override val code: Int,
    override val message: String,
    val kind: String,
) : BaseResponse