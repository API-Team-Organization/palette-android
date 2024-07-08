package com.example.palette.data.auth

data class RegisterRequest(
    val email: String,
    val password: String,
    val birthDate: String,
    val username: String,
)
