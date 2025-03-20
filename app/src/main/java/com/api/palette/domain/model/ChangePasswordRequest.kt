package com.api.palette.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val beforePassword: String,
    val afterPassword: String
)
