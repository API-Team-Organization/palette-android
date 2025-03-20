package com.api.palette.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class BaseResponse {
    abstract val code: Int
    abstract val message: String
}

@Serializable
data class DataResponse<T>(
    override val code: Int,
    override val message: String,
    val data: T
) : BaseResponse()

@Serializable
data class VoidResponse(
    override val code: Int,
    override val message: String
) : BaseResponse()

@Serializable
data class ErrorResponse(
    override val code: Int,
    override val message: String,
    val kind: String
) : BaseResponse()
