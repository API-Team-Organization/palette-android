package com.example.palette.data.auth

data class ChangePasswordRequest(
    val beforePassword: String,
    val afterPassword: String
)
