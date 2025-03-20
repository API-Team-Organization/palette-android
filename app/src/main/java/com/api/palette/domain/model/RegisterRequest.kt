package com.api.palette.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val birthDate: String,
    val username: String
)
