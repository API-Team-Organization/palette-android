package com.api.palette.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val beforePassword: String,
    val afterPassword: String
)
