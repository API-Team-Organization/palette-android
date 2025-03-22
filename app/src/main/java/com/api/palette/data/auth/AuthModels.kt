package com.api.palette.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val birthDate: String,
    val username: String,
)

@Serializable
data class VerifyRequest(val code: String)

@Serializable
data class ChangePasswordRequest(
    val beforePassword: String,
    val afterPassword: String
)
