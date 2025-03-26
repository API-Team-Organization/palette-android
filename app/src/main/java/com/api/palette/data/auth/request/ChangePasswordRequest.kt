package com.api.palette.data.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val beforePassword: String,
    val afterPassword: String
)
